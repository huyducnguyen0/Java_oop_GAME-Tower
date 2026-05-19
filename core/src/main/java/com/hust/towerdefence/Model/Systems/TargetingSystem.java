package com.hust.towerdefence.Model.Systems;

import com.badlogic.gdx.utils.Array;
import com.hust.towerdefence.Model.Entities.BaseEntity.Team;
import com.hust.towerdefence.Model.Entities.Combat.CombatEntity;
import com.hust.towerdefence.Model.Entities.Combat.Soldier.Miner;
import com.hust.towerdefence.Model.Managers.EntityManager;


public class TargetingSystem {
    private EntityManager entityManager;

    public TargetingSystem(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void update(float delta) {
        Array<CombatEntity> combatants = entityManager.getAllActiveCombatUnits();
        for (CombatEntity entity : combatants) {
            // Bỏ qua nếu đã chết (active=false) hoặc removed
            if (entity.isDead() || entity.isRemoved() || entity instanceof Miner) continue;
            long targetId = entity.getTargetId();
            CombatEntity currentTarget = null;
            if(targetId != -1){
                currentTarget = entityManager.getEntityById(targetId, CombatEntity.class);
            }
            // Kiểm tra mục tiêu hiện tại còn giá trị không
            if (currentTarget != null) {
                if (currentTarget.isDead() || currentTarget.isRemoved() || currentTarget.getTeam() == entity.getTeam() || currentTarget instanceof Miner) {
                    entity.setTargetId(-1);
                    currentTarget = null;
                }
            } else if (targetId != -1) {
                // ID không tìm thấy -> xóa target
                entity.setTargetId(-1);
            }

            // Nếu không có mục tiêu, tìm mới
            if (entity.getTargetId() == -1) {
                CombatEntity newTarget = findNearestEnemy(entity);
                if (newTarget != null) {
                    entity.setTargetId(newTarget.getId());
                }
            }
        }
    }

    public CombatEntity findNearestEnemy(CombatEntity source) {
        Team myTeam = source.getTeam();
        CombatEntity nearest = null;
        float minDist = Float.MAX_VALUE;

        for (CombatEntity other : entityManager.getAllActiveCombatUnits()) {
            if (other == source || other.isDead() || other.isRemoved()) continue;
            if (other.getTeam() == myTeam) continue; // cùng phe -> bỏ qua
            if (other instanceof Miner) continue;
            float dist = source.getPosition().dst2(other.getPosition());
            // Chỉ chọn nếu trong tầm tấn công (dùng bình phương để so sánh)
            float rangeSq = source.getAttackRange() * source.getAttackRange();
            if (dist <= rangeSq && dist < minDist) {
                minDist = dist;
                nearest = other;
            }
        }
        return nearest;
    }
}
