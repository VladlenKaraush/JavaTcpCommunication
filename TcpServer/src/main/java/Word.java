import javax.persistence.*;


@Entity

@Table(name = "word")
public class Word {


    private int wordId;
    private String word;
    private Description description;

    public Word(){};

    public Word(int wordId, String word, Description description){
        this.wordId = wordId;
        this.word = word;
        this.description = description;
    }

    @OneToOne
    @JoinColumn(name = "description_id")
    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "word_id")
    public int getWordId(){

        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    @Column(name = "word")
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }


    @Override
    public String toString() {
        return this.word;
    }
}
