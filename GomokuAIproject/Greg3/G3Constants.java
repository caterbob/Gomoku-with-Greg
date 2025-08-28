package GomokuAIproject.Greg3;

public final class G3Constants {

    public static final int GAME_WILL_BE_OVER = 99998;
    public static final int GAME_OVER = 99999;
    public static final int GAME_DRAWN = 500000;

    // LINE LOCATIONS
    public static final int[] row0 = { 169, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 169 };
    public static final int[] row1 = { 169, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 169 };
    public static final int[] row2 = { 169, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 169 };
    public static final int[] row3 = { 169, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 169 };
    public static final int[] row4 = { 169, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 169 };
    public static final int[] row5 = { 169, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 169 };
    public static final int[] row6 = { 169, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 169 };
    public static final int[] row7 = { 169, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 169 };
    public static final int[] row8 = { 169, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 169 };
    public static final int[] row9 = { 169, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 169 };
    public static final int[] row10 = { 169, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 169 };
    public static final int[] row11 = { 169, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 169 };
    public static final int[] row12 = { 169, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169 };

    public static final int[] column0 = { 169, 0, 13, 26, 39, 52, 65, 78, 91, 104, 117, 130, 143, 156, 169 };
    public static final int[] column1 = { 169, 1, 14, 27, 40, 53, 66, 79, 92, 105, 118, 131, 144, 157, 169 };
    public static final int[] column2 = { 169, 2, 15, 28, 41, 54, 67, 80, 93, 106, 119, 132, 145, 158, 169 };
    public static final int[] column3 = { 169, 3, 16, 29, 42, 55, 68, 81, 94, 107, 120, 133, 146, 159, 169 };
    public static final int[] column4 = { 169, 4, 17, 30, 43, 56, 69, 82, 95, 108, 121, 134, 147, 160, 169 };
    public static final int[] column5 = { 169, 5, 18, 31, 44, 57, 70, 83, 96, 109, 122, 135, 148, 161, 169 };
    public static final int[] column6 = { 169, 6, 19, 32, 45, 58, 71, 84, 97, 110, 123, 136, 149, 162, 169 };
    public static final int[] column7 = { 169, 7, 20, 33, 46, 59, 72, 85, 98, 111, 124, 137, 150, 163, 169 };
    public static final int[] column8 = { 169, 8, 21, 34, 47, 60, 73, 86, 99, 112, 125, 138, 151, 164, 169 };
    public static final int[] column9 = { 169, 9, 22, 35, 48, 61, 74, 87, 100, 113, 126, 139, 152, 165, 169 };
    public static final int[] column10 = { 169, 10, 23, 36, 49, 62, 75, 88, 101, 114, 127, 140, 153, 166, 169 };
    public static final int[] column11 = { 169, 11, 24, 37, 50, 63, 76, 89, 102, 115, 128, 141, 154, 167, 169 };
    public static final int[] column12 = { 169, 12, 25, 38, 51, 64, 77, 90, 103, 116, 129, 142, 155, 168, 169 };

    // (/ diagonals)
    public static final int[] forwardDiagonal0 = { 169, 52, 40, 28, 16, 4, 169 };
    public static final int[] forwardDiagonal1 = { 169, 65, 53, 41, 29, 17, 5, 169 };
    public static final int[] forwardDiagonal2 = { 169, 78, 66, 54, 42, 30, 18, 6, 169 };
    public static final int[] forwardDiagonal3 = { 169, 91, 79, 67, 55, 43, 31, 19, 7, 169 };
    public static final int[] forwardDiagonal4 = { 169, 104, 92, 80, 68, 56, 44, 32, 20, 8, 169 };
    public static final int[] forwardDiagonal5 = { 169, 117, 105, 93, 81, 69, 57, 45, 33, 21, 9, 169 };
    public static final int[] forwardDiagonal6 = { 169, 130, 118, 106, 94, 82, 70, 58, 46, 34, 22, 10, 169 };
    public static final int[] forwardDiagonal7 = { 169, 143, 131, 119, 107, 95, 83, 71, 59, 47, 35, 23, 11, 169 };
    public static final int[] forwardDiagonal8 = { 169, 156, 144, 132, 120, 108, 96, 84, 72, 60, 48, 36, 24, 12, 169 };
    public static final int[] forwardDiagonal9 = { 169, 157, 145, 133, 121, 109, 97, 85, 73, 61, 49, 37, 25, 169 };
    public static final int[] forwardDiagonal10 = { 169, 158, 146, 134, 122, 110, 98, 86, 74, 62, 50, 38, 169 };
    public static final int[] forwardDiagonal11 = { 169, 159, 147, 135, 123, 111, 99, 87, 75, 63, 51, 169 };
    public static final int[] forwardDiagonal12 = { 169, 160, 148, 136, 124, 112, 100, 88, 76, 64, 169 };
    public static final int[] forwardDiagonal13 = { 169, 161, 149, 137, 125, 113, 101, 89, 77, 169 };
    public static final int[] forwardDiagonal14 = { 169, 162, 150, 138, 126, 114, 102, 90, 169 };
    public static final int[] forwardDiagonal15 = { 169, 163, 151, 139, 127, 115, 103, 169 };
    public static final int[] forwardDiagonal16 = { 169, 164, 152, 140, 128, 116, 169 };

    public static final int[] backwardDiagonal0 = { 169, 64, 50, 36, 22, 8, 169 };
    public static final int[] backwardDiagonal1 = { 169, 77, 63, 49, 35, 21, 7, 169 };
    public static final int[] backwardDiagonal2 = { 169, 90, 76, 62, 48, 34, 20, 6, 169 };
    public static final int[] backwardDiagonal3 = { 169, 103, 89, 75, 61, 47, 33, 19, 5, 169 };
    public static final int[] backwardDiagonal4 = { 169, 116, 102, 88, 74, 60, 46, 32, 18, 4, 169 };
    public static final int[] backwardDiagonal5 = { 169, 129, 115, 101, 87, 73, 59, 45, 31, 17, 3, 169 };
    public static final int[] backwardDiagonal6 = { 169, 142, 128, 114, 100, 86, 72, 58, 44, 30, 16, 2, 169 };
    public static final int[] backwardDiagonal7 = { 169, 155, 141, 127, 113, 99, 85, 71, 57, 43, 29, 15, 1, 169 };
    public static final int[] backwardDiagonal8 = { 169, 168, 154, 140, 126, 112, 98, 84, 70, 56, 42, 28, 14, 0, 169 };
    public static final int[] backwardDiagonal9 = { 169, 167, 153, 139, 125, 111, 97, 83, 69, 55, 41, 27, 13, 169 };
    public static final int[] backwardDiagonal10 = { 169, 166, 152, 138, 124, 110, 96, 82, 68, 54, 40, 26, 169 };
    public static final int[] backwardDiagonal11 = { 169, 165, 151, 137, 123, 109, 95, 81, 67, 53, 39, 169 };
    public static final int[] backwardDiagonal12 = { 169, 164, 150, 136, 122, 108, 94, 80, 66, 52, 169 };
    public static final int[] backwardDiagonal13 = { 169, 163, 149, 135, 121, 107, 93, 79, 65, 169 };
    public static final int[] backwardDiagonal14 = { 169, 162, 148, 134, 120, 106, 92, 78, 169 };
    public static final int[] backwardDiagonal15 = { 169, 161, 147, 133, 119, 105, 91, 169 };
    public static final int[] backwardDiagonal16 = { 169, 160, 146, 132, 118, 104, 169 };

    // SEGMENT VALUES
    public static final int EMPTY_VALUE = 0;
    public static final int SINGLE_1_VALUE = 0;

    public static final int CONNECTED_2_VALUE = 20; // ex: _ _ X X _    (0 space)
    public static final int DISCONNECTED_2_VALUE = 20; // ex: _ X _ X _ (1 space)
    public static final int GAP_2_VALUE = 20; // ex: X _ _ X _  (2 spaces)
    public static final int SEVERED_2_VALUE = 20; // ex: X _ _ _ X  (3 spaces)
    
    public static final int CONNECTED_3_VALUE = 50; // ex: X X X _ _ (0 space)
    public static final int DISCONNECTED_3_VALUE = 50; // ex: X _ X X _ (1 space)
    public static final int GAP_3_VALUE = 50;   // ex: X _ _ X X (2 spaces)
    public static final int BROKEN_3_VALUE = 50; // ex: X _ X _ X (2 spaces spread out
    
    public static final int CONNECTED_4_VALUE = 80; // ex: X X X X _ (0 space)
    public static final int DISCONNECTED_4_VALUE = 80; // ex: X _ X X X (1 space)

    public static final int WIN_VALUE = 99999; // ex: X X X X X

    // NEW SEGMENT VALUES
    public static final int NEW_EMPTY_VALUE = 0;
    public static final int NEW_SINGLE_1_VALUE = 2;

    public static final int NEW_CONNECTED_2_VALUE = 20; // ex: _ _ X X _    (0 space)
    public static final int NEW_DISCONNECTED_2_VALUE = (int)Math.round(NEW_CONNECTED_2_VALUE * 0.85); // ex: _ X _ X _ (1 space)
    public static final int NEW_GAP_2_VALUE = (int)Math.round(NEW_CONNECTED_2_VALUE * 0.5); // ex: X _ _ X _  (2 spaces)
    public static final int NEW_SEVERED_2_VALUE = (int)Math.round(NEW_CONNECTED_2_VALUE * 0.5); // ex: X _ _ _ X  (3 spaces)

    public static final int NEW_CONNECTED_3_VALUE = 58; // ex: X X X _ _ (0 space)
    public static final int NEW_DISCONNECTED_3_VALUE = 52; // ex: X _ X X _ (1 space)
    public static final int NEW_GAP_3_VALUE = 52;   // ex: X _ _ X X (2 spaces)
    public static final int NEW_BROKEN_3_VALUE = 52; // ex: X _ X _ X (2 spaces spread out)

    public static final int NEW_CONNECTED_4_VALUE = 80; // ex: X X X X _ (0 space)
    public static final int NEW_DISCONNECTED_4_VALUE = 80; // ex: X _ X X X (1 space)

    public static final int NEW_WIN_VALUE = 99999; // ex: X X X X X


    // Threat Map List Indexes
    public static final int FIVE_THREAT_INDEX = 0;
    public static final int OPEN_FOUR_THREAT_INDEX = 1;
    public static final int FOUR_THREAT_INDEX = 2;
    public static final int THREE_THREAT_INDEX = 3;

    // SEGMENT SCORE TABLE
    // 5-bit mask representing 1st stone, 2nd stone, ...
    public static final int[] SEGMENT_SCORE_TABLE = {
        EMPTY_VALUE,   // 00000
        SINGLE_1_VALUE,    // 00001
        SINGLE_1_VALUE,    // 00010
        CONNECTED_2_VALUE,    // 00011
        SINGLE_1_VALUE,    // 00100
        DISCONNECTED_2_VALUE,    // 00101
        CONNECTED_2_VALUE,    // 00110
        CONNECTED_3_VALUE,    // 00111
        SINGLE_1_VALUE,    // 01000
        GAP_2_VALUE,    // 01001
        DISCONNECTED_2_VALUE,    // 01010
        DISCONNECTED_3_VALUE,    // 01011
        CONNECTED_2_VALUE,    // 01100
        DISCONNECTED_3_VALUE,    // 01101
        CONNECTED_3_VALUE,    // 01110
        CONNECTED_4_VALUE,    // 01111
        SINGLE_1_VALUE,    // 10000
        SEVERED_2_VALUE,    // 10001
        GAP_2_VALUE,    // 10010
        GAP_3_VALUE,    // 10011
        DISCONNECTED_2_VALUE,    // 10100
        BROKEN_3_VALUE,    // 10101
        DISCONNECTED_3_VALUE,    // 10110
        DISCONNECTED_4_VALUE,    // 10111
        CONNECTED_2_VALUE,    // 11000
        GAP_3_VALUE,    // 11001
        DISCONNECTED_3_VALUE,    // 11010
        DISCONNECTED_4_VALUE,    // 11011
        CONNECTED_3_VALUE,    // 11100
        DISCONNECTED_4_VALUE,    // 11101
        CONNECTED_4_VALUE,    // 11110
        WIN_VALUE,    // 11111
    };

    public static final int[] TEST_SEGMENT_SCORE_TABLE = {
        NEW_EMPTY_VALUE,            // 00000
        NEW_SINGLE_1_VALUE,         // 00001
        NEW_SINGLE_1_VALUE,         // 00010
        NEW_CONNECTED_2_VALUE,      // 00011
        NEW_SINGLE_1_VALUE,         // 00100
        NEW_DISCONNECTED_2_VALUE,   // 00101
        NEW_CONNECTED_2_VALUE,      // 00110
        NEW_CONNECTED_3_VALUE,      // 00111
        NEW_SINGLE_1_VALUE,         // 01000
        NEW_GAP_2_VALUE,            // 01001
        NEW_DISCONNECTED_2_VALUE,   // 01010
        NEW_DISCONNECTED_3_VALUE,   // 01011
        NEW_CONNECTED_2_VALUE,      // 01100
        NEW_DISCONNECTED_3_VALUE,   // 01101
        NEW_CONNECTED_3_VALUE,      // 01110
        NEW_CONNECTED_4_VALUE,      // 01111
        NEW_SINGLE_1_VALUE,         // 10000
        NEW_SEVERED_2_VALUE,        // 10001
        NEW_GAP_2_VALUE,            // 10010
        NEW_GAP_3_VALUE,            // 10011
        NEW_DISCONNECTED_2_VALUE,   // 10100
        NEW_BROKEN_3_VALUE,         // 10101
        NEW_DISCONNECTED_3_VALUE,   // 10110
        NEW_DISCONNECTED_4_VALUE,   // 10111
        NEW_CONNECTED_2_VALUE,      // 11000
        NEW_GAP_3_VALUE,            // 11001
        NEW_DISCONNECTED_3_VALUE,   // 11010
        NEW_DISCONNECTED_4_VALUE,   // 11011
        NEW_CONNECTED_3_VALUE,      // 11100
        NEW_DISCONNECTED_4_VALUE,   // 11101
        NEW_CONNECTED_4_VALUE,      // 11110
        NEW_WIN_VALUE,              // 11111
    };
    
    // Threat table codes. 2nd number in each int[] that help distinguish what type of threat the locations are
    public static final int SKIP_CODE = 0;
    public static final int FIVE_THREAT_CODE = 1;
    public static final int OPEN_FOUR_THREAT_CODE = 2;
    public static final int FOUR_THREAT_CODE = 3;
    public static final int THREE_THREAT_CODE = 4;
    public static final int NONEXISTENT_CODE = 9;   // double use case for if only 1 open 4 threat

    // 7-bit mask representing left of segment empty, right of segment empty, 1st stone, 2nd stone, ...
    // 0 = empty, 1 = not empty
    public static final int[][] SEGMENT_THREAT_TABLE = { 
        {SKIP_CODE},    // 00 00000
        {SKIP_CODE},    // 00 00001
        {SKIP_CODE},    // 00 00010
        {THREE_THREAT_CODE, -3, -4, -5},    // 00 00011
        {SKIP_CODE},    // 00 00100
        {THREE_THREAT_CODE, -2, -4, -5},    // 00 00101
        {THREE_THREAT_CODE, -1, -4, -5},    // 00 00110
        {OPEN_FOUR_THREAT_CODE, -4, NONEXISTENT_CODE, -4, -5},    // 00 00111
        {SKIP_CODE},    // 00 01000
        {THREE_THREAT_CODE, -2, -3, -5},    // 00 01001
        {THREE_THREAT_CODE, -1, -3, -5},    // 00 01010
        {OPEN_FOUR_THREAT_CODE, -3, NONEXISTENT_CODE, -3, -5},    // 00 01011
        {THREE_THREAT_CODE, -1, -2, -5},    // 00 01100
        {OPEN_FOUR_THREAT_CODE, -2, NONEXISTENT_CODE, -2, -5},    // 00 01101
        {OPEN_FOUR_THREAT_CODE, -1, -5, -1, -5},    // 00 01110
        {FIVE_THREAT_CODE, -5},   // 00 01111
        {SKIP_CODE},    // 00 10000
        {THREE_THREAT_CODE, -2, -3, -4},    // 00 10001
        {THREE_THREAT_CODE, -1, -3, -4},    // 00 10010
        {FOUR_THREAT_CODE, -3, -4},    // 00 10011
        {THREE_THREAT_CODE, -1, -2, -4},    // 00 10100
        {FOUR_THREAT_CODE, -2, -4},    // 00 10101
        {OPEN_FOUR_THREAT_CODE, -4, NONEXISTENT_CODE, -1, -4},    // 00 10110
        {FIVE_THREAT_CODE, -4},    // 00 10111
        {THREE_THREAT_CODE, -1, -2, -3},    // 00 11000
        {FOUR_THREAT_CODE, -2, -3},    // 00 11001
        {OPEN_FOUR_THREAT_CODE, -3, NONEXISTENT_CODE, -1, -3},    // 00 11010
        {FIVE_THREAT_CODE, -3},    // 00 11011
        {OPEN_FOUR_THREAT_CODE, -2, NONEXISTENT_CODE, -1, -2},    // 00 11100
        {FIVE_THREAT_CODE, -2},    // 00 11101
        {FIVE_THREAT_CODE, -1},    // 00 11110
        {SKIP_CODE},    // 00 11111
        // new 32
        {SKIP_CODE},    // 0100000
        {SKIP_CODE},    // 0100001
        {SKIP_CODE},    // 0100010
        {THREE_THREAT_CODE, -3, -4, -5},    // 0100011
        {SKIP_CODE},    // 0100100
        {THREE_THREAT_CODE, -2, -4, -5},    // 0100101
        {THREE_THREAT_CODE, -1, -4, -5},    // 0100110
        {FOUR_THREAT_CODE, -4, -5},    // 0100111
        {SKIP_CODE},    // 0101000
        {THREE_THREAT_CODE, -2, -3, -5},    // 0101001
        {THREE_THREAT_CODE, -1, -3, -5},    // 0101010
        {FOUR_THREAT_CODE, -3, -5},    // 0101011
        {THREE_THREAT_CODE, -1, -2, -5},    // 0101100
        {FOUR_THREAT_CODE, -2, -5},    // 0101101
        {OPEN_FOUR_THREAT_CODE, -5, NONEXISTENT_CODE, -1, -5},    // 01 01110
        {FIVE_THREAT_CODE, -5},   // 0101111
        {SKIP_CODE},    // 0110000
        {THREE_THREAT_CODE, -2, -3, -4},    // 0110001
        {THREE_THREAT_CODE, -1, -3, -4},    // 0110010
        {FOUR_THREAT_CODE, -3, -4},    // 0110011
        {THREE_THREAT_CODE, -1, -2, -4},    // 0110100
        {FOUR_THREAT_CODE, -2, -4},    // 0110101
        {OPEN_FOUR_THREAT_CODE, -4, NONEXISTENT_CODE, -1, -4},    // 01 10110
        {FIVE_THREAT_CODE, -4},    // 0110111
        {THREE_THREAT_CODE, -1, -2, -3},    // 0111000
        {FOUR_THREAT_CODE, -2, -3},    // 0111001
        {OPEN_FOUR_THREAT_CODE, -3, NONEXISTENT_CODE, -1, -3},    // 01 11010
        {FIVE_THREAT_CODE, -3},    // 0111011
        {OPEN_FOUR_THREAT_CODE, -2, NONEXISTENT_CODE, -1, -2},    // 01 11100
        {FIVE_THREAT_CODE, -2},    // 0111101
        {FIVE_THREAT_CODE, -1},    // 0111110
        {SKIP_CODE},    // 0111111
        // new 32
        {SKIP_CODE},    // 1000000
        {SKIP_CODE},    // 1000001
        {SKIP_CODE},    // 1000010
        {THREE_THREAT_CODE, -3, -4, -5},    // 1000011
        {SKIP_CODE},    // 1000100
        {THREE_THREAT_CODE, -2, -4, -5},    // 1000101
        {THREE_THREAT_CODE, -1, -4, -5},    // 1000110
        {OPEN_FOUR_THREAT_CODE, -4, NONEXISTENT_CODE, -4, -5},    // 10 00111
        {SKIP_CODE},    // 1001000
        {THREE_THREAT_CODE, -2, -3, -5},    // 1001001
        {THREE_THREAT_CODE, -1, -3, -5},    // 1001010
        {OPEN_FOUR_THREAT_CODE, -3, NONEXISTENT_CODE, -3, -5},    // 10 01011
        {THREE_THREAT_CODE, -1, -2, -5},    // 1001100
        {OPEN_FOUR_THREAT_CODE, -2, NONEXISTENT_CODE, -2, -5},    // 10 01101
        {OPEN_FOUR_THREAT_CODE, -1, NONEXISTENT_CODE, -1, -5},    // 10 01110
        {FIVE_THREAT_CODE, -5},   // 1001111
        {SKIP_CODE},    // 1010000
        {THREE_THREAT_CODE, -2, -3, -4},    // 1010001
        {THREE_THREAT_CODE, -1, -3, -4},    // 1010010
        {FOUR_THREAT_CODE, -3, -4},    // 1010011
        {THREE_THREAT_CODE, -1, -2, -4},    // 1010100
        {FOUR_THREAT_CODE, -2, -4},    // 1010101
        {FOUR_THREAT_CODE, -1, -4},    // 10 10110
        {FIVE_THREAT_CODE, -4},    // 1010111
        {THREE_THREAT_CODE, -1, -2, -3},    // 1011000
        {FOUR_THREAT_CODE, -2, -3},    // 1011001
        {FOUR_THREAT_CODE, -1, -3},    // 10 11010
        {FIVE_THREAT_CODE, -3},    // 1011011
        {FOUR_THREAT_CODE, -1, -2},    // 10 11100
        {FIVE_THREAT_CODE, -2},    // 1011101
        {FIVE_THREAT_CODE, -1},    // 1011110
        {SKIP_CODE},    // 1011111
        // new 32
        {SKIP_CODE},    // 1100000
        {SKIP_CODE},    // 1100001
        {SKIP_CODE},    // 1100010
        {THREE_THREAT_CODE, -3, -4, -5},    // 1100011
        {SKIP_CODE},    // 1100100
        {THREE_THREAT_CODE, -2, -4, -5},    // 1100101
        {THREE_THREAT_CODE, -1, -4, -5},    // 1100110
        {FOUR_THREAT_CODE, -4, -5},    // 11 00111
        {SKIP_CODE},    // 1101000
        {THREE_THREAT_CODE, -2, -3, -5},    // 1101001
        {THREE_THREAT_CODE, -1, -3, -5},    // 1101010
        {FOUR_THREAT_CODE, -3, -5},    // 11 01011
        {THREE_THREAT_CODE, -1, -2, -5},    // 1101100
        {FOUR_THREAT_CODE, -2, -5},    // 11 01101
        {FOUR_THREAT_CODE, -1, -5},    // 11 01110
        {FIVE_THREAT_CODE, -5},   // 1101111
        {SKIP_CODE},    // 1110000
        {THREE_THREAT_CODE, -2, -3, -4},    // 1110001
        {THREE_THREAT_CODE, -1, -3, -4},    // 1110010
        {FOUR_THREAT_CODE, -3, -4},    // 1110011
        {THREE_THREAT_CODE, -1, -2, -4},    // 1110100
        {FOUR_THREAT_CODE, -2, -4},    // 1110101
        {FOUR_THREAT_CODE, -1, -4},    // 11 10110
        {FIVE_THREAT_CODE, -4},    // 1110111
        {THREE_THREAT_CODE, -1, -2, -3},    // 1111000
        {FOUR_THREAT_CODE, -2, -3},    // 1111001
        {FOUR_THREAT_CODE, -1, -3},    // 11 11010
        {FIVE_THREAT_CODE, -3},    // 1111011
        {FOUR_THREAT_CODE, -1, -2},    // 11 11100
        {FIVE_THREAT_CODE, -2},    // 1111101
        {FIVE_THREAT_CODE, -1},    // 1111110
        {SKIP_CODE}    // 1111111
    };
}
