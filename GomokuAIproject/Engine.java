package GomokuAIproject;
public interface Engine {
    public int playFromPosition(Board position);
    public int playFromPositionHuman(Board position);
    public void setIsOpponentBlack(boolean isOpponentBlack);
    public boolean getIsOpponentBlack();
}