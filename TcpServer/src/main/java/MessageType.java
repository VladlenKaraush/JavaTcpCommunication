public enum MessageType {
    FETCH_DESCRIPTION_BY_WORD("F"), SEARCH_FOR_WORD_BY_MASK("S"), NEW_WORD("N"), UPDATE_WORD("U"), DELETE_WORD("D"), END_CONNECTION("E");

    private String letter;

    public String getLetter(){
        return letter;
    }

    MessageType(String e) {
        this.letter = e;
    }
}
