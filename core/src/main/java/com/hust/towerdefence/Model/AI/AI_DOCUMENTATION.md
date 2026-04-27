# 🤖 AI SYSTEM DOCUMENTATION

## Tổng quan
AI System điều khiển logic hành động của tất cả Enemy trong game.

## Các Components

### 1. AIController.java
- Quản lý behavior của một Enemy
- Hỗ trợ các trạng thái: IDLE, CHASE, ATTACK, RETREAT
- Tự động quyết định hành động mỗi 1 giây

### 2. AISystem.java
- Update AI logic cho tất cả Enemies
- Gọi AIController.update() cho mỗi enemy

### 3. Integration
- Integrate trong BoovGameScreen.render()
- Gọi trước CombatSystem để quyết định hành động

## AI Behaviors

### CHASE: Đuổi theo Unit
- Tính vector hướng tới Unit
- Di chuyển về phía Unit
- Trigger khi: Unit trong tầm nhìn + HP đủ

### ATTACK: Tấn công Unit
- Đứng yên tấn công
- Gây damage mỗi 0.5 giây
- Trigger khi: Unit rất gần + HP cao

### RETREAT: Rút lui
- Chạy trốn xa từ Unit
- Tăng tốc độ di chuyển
- Trigger khi: HP thấp (< 20%)

### IDLE: Chờ đợi
- Không làm gì

## Decision Making

Enemy quyết định hành động dựa trên:
1. **Tìm Unit gần nhất**
2. **So sánh khoảng cách** đến Unit và đến BASE
3. **Kiểm tra HP** có đủ để tấn công hay không

```
Nếu HP > 50% và Unit gần < 100 units
  → ATTACK
Nếu HP > 30% và Unit gần < 200 units
  → CHASE
Nếu HP < 20% và BASE xa hơn Unit
  → RETREAT
Khác
  → CHASE (mặc định)
```

## Spawn Random

- Enemy xuất hiện từ 4 cạnh bản đồ (random)
- Stats được scale theo wave
- AIController khởi tạo ngay khi Enemy spawn

## Performance

- Update 1 lần mỗi 1 giây (decision timer)
- Frame-by-frame execution action (smooth movement)
- Không dùng heavy pathfinding (simple vector math)

## Usage

```java
// Trong BoovGameScreen.render()
aiSystem.update(gameWorld, delta);

// AIController auto-update:
for (Enemy e : enemies) {
    e.getAIController().update(world, em, delta);
}
```

## Mở rộng

### Thêm behaviors mới:
```java
case PATROL:
    // Đi tuần tra
    break;
case RUSH:
    // Tấn công bạo lực
    break;
```

### Optimize decision:
- Dùng entity hashing để tìm nearest unit nhanh hơn
- Implement spatial partitioning (grid-based)
- Cache khoảng cách để tránh tính lại nhiều lần

### Improve pathfinding:
- Dùng A* algorithm với gdx-ai
- Tạo waypoints để avoid obstacles
- Implement steering behaviors (gdx-ai có sẵn)

