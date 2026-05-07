package com.hust.towerdefence.Model.System;

import com.badlogic.gdx.ai.steer.Proximity;
import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.ai.steer.behaviors.Separation;
import com.badlogic.gdx.ai.steer.proximities.RadiusProximity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hust.towerdefence.Model.Entities.BaseEntity;
import com.hust.towerdefence.Model.Entities.SteerableEntity;
import com.hust.towerdefence.Model.Manager.EntityManager;

/**
 * MOVEMENT SYSTEM - Quản lý di chuyển và vật lý bầy đàn.
 * Đã sửa lỗi Nested Iterator bằng cách dùng index-based loop.
 */
public class MovementSystem {

    private final EntityManager entityManager;
    private final Array<SteerableEntity> activeSteerables;

    public MovementSystem(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.activeSteerables = new Array<>();
    }

    public void update(float delta) {
        // 1. Lấy danh sách tổng từ EntityManager
        Array<BaseEntity> allEntities = entityManager.getAllEntities();

        // Lọc danh sách thực thể có khả năng di chuyển
        activeSteerables.clear();

        // Dùng vòng lặp for i để tránh GdxRuntimeException: #iterator() cannot be used nested
        for (int i = 0; i < allEntities.size; i++) {
            BaseEntity entity = allEntities.get(i);
            if (entity instanceof SteerableEntity && entity.isActive()) {
                activeSteerables.add((SteerableEntity) entity);
            }
        }

        // 2. Cập nhật logic cho từng thực thể
        for (int i = 0; i < activeSteerables.size; i++) {
            SteerableEntity entity = activeSteerables.get(i);

            // Cập nhật bộ não (FSM)
            if (entity.getStateMachine() != null) {
                entity.getStateMachine().update();
            }

            // Thiết lập né đồng đội (Flocking) - Chỉ tính toán nếu cần thiết
            applyFlockingBehaviors(entity);

            // Thực thi di chuyển vật lý (Tích hợp delta thời gian)
            entity.update(delta);
        }
    }

    private void applyFlockingBehaviors(SteerableEntity entity) {
        // Nếu thực thể đứng yên hoặc không có vận tốc đáng kể, bỏ qua né tránh để tối ưu CPU
        if (entity.getLinearVelocity().isZero(0.1f)) return;

        // Tạo vùng quét xung quanh bằng RadiusProximity
        // Ép kiểu Proximity<Vector2> để tương thích với Behavior của LibGDX AI
        Proximity<Vector2> proximity = new RadiusProximity<Vector2>(
            entity,
            activeSteerables,
            entity.getBoundingRadius() * 5.0f
        );

        // Lực đẩy tách biệt (Separation) giúp các Unit không chồng lấn lên nhau
        Separation<Vector2> separation = new Separation<>(entity, proximity);

        // Hệ thống lái ưu tiên (PrioritySteering)
        // Ưu tiên 0: Né tránh (Separation) - Ưu tiên 1: Đi theo đường (FollowPath)
        PrioritySteering<Vector2> prioritySteering = new PrioritySteering<>(entity);
        prioritySteering.add(separation);

        if (entity.getFollowPathBehavior() != null) {
            prioritySteering.add(entity.getFollowPathBehavior());
        }

        entity.setSteeringBehavior(prioritySteering);
    }
}
