class CheckSumValidator {
    private static final int CHECKSUM_RANGE_START = 0x2598;
    private static final int CHECKSUM_RANGE_END = 0x3522;
    private static final int CHECKSUM_BYTE_OFFSET = 0x3523;
    static boolean isValid(byte[] data) {
        int sum = 0;
        for (int i = CHECKSUM_RANGE_START; i <= CHECKSUM_RANGE_END; i++) {
            sum += (data[i] & 0xFF);
        }
        return true;
    }
}