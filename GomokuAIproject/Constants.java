package GomokuAIproject;

public class Constants {
    public static class BoardConstants{
        // space possibilities
        public static final int EMPTY = 0;
        public static final int BLACK = 1;
        public static final int WHITE = 2;

        // game states
        public static final int ONGOING = 0;
        public static final int BLACK_WINS = 1;
        public static final int WHITE_WINS = 2;
        public static final int TIE = 3;
    }

    public static class EvaluationConstants{
        public static final int GAME_OVER = 9999;
        public static final int GAME_WILL_BE_OVER = 9998;
        public static final int GAME_DRAWN = 50000;

        public static class G2{
             // pattern counts indexes
            public static final int BLACK_LINE_OF_2 = 0;
            public static final int WHITE_LINE_OF_2 = 1;
            public static final int BLACK_LINE_OF_3 = 2;
            public static final int WHITE_LINE_OF_3 = 3;
            public static final int BLACK_SPECIAL_3 = 4;
            public static final int WHITE_SPECIAL_3 = 5;
            public static final int BLACK_LINE_OF_4 = 6;
            public static final int WHITE_LINE_OF_4 = 7;
            public static final int WIN_PRESENT = 8;

            public static final int LINE_OF_2_VALUE = 2;
            public static final int LINE_OF_3_VALUE = 5;
            public static final int LINE_OF_4_VALUE = 8;

        }

    }

    public static class OffsetConstants{
        // meta offsets
        public static final int META_UP = -15;
        public static final int META_RIGHT = 1;
        public static final int META_LEFT = -1;
        public static final int META_DOWN = 15;
        public static final int META_UPRIGHT = META_UP + META_RIGHT;
        public static final int META_UPLEFT = META_UP + META_LEFT;
        public static final int META_DOWNRIGHT = META_DOWN + META_RIGHT;
        public static final int META_DOWNLEFT = META_DOWN + META_LEFT;
        public static final int[] META_OFFSETS = {META_UP, META_RIGHT, META_DOWN, META_LEFT, META_UPRIGHT, META_UPLEFT, META_DOWNRIGHT, META_DOWNLEFT};

        // real offsets
        public static final int REAL_UP = -13;
        public static final int REAL_RIGHT = 1;
        public static final int REAL_LEFT = -1;
        public static final int REAL_DOWN = 13;
        public static final int REAL_UPRIGHT = REAL_UP + REAL_RIGHT;
        public static final int REAL_UPLEFT = REAL_UP + REAL_LEFT;
        public static final int REAL_DOWNRIGHT = REAL_DOWN + REAL_RIGHT;
        public static final int REAL_DOWNLEFT = REAL_DOWN + REAL_LEFT;
        public static final int[] REAL_OFFSETS = {REAL_UP, REAL_RIGHT, REAL_DOWN, REAL_LEFT, REAL_UPRIGHT, REAL_UPLEFT, REAL_DOWNRIGHT, REAL_DOWNLEFT};
    }
}
