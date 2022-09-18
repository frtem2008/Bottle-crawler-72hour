package GameObjects;
//Все параметры и константы (все настроено, не менять)
public enum Constants {
    BlockSize(100),

    LadderClimbSpeed(40),
    SlimeBounce(2),

    PlayerSize(75),
    PlayerMaxXSpeed(6),
    PlayerMaxYSpeed(12),
    PlayerJumpHeight(8.5),
    PlayerGravitation(0.3),
    EnablePlayerWallJump(1),
    MaxFallingHeight(1500),

    DefaultCameraX(500),
    DefaultCameraY(28),
    MinCameraY(500),
    MaxCameraY(220),

    PlatformMoveSpeed(2),
    ;

    public double getValue() {
        return value;
    }

    private double value;

    Constants(double value) {
        this.value = value;
    }
}
