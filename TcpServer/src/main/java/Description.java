import javax.persistence.*;


@Entity
@Table(name = "description")
public class Description {

    private int descriptionId;
    private String description;
    private Word word;

    @OneToOne(mappedBy = "description")
    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public Description(){};

    public Description(int descriptionId, String description) {
        this.descriptionId = descriptionId;
        this.description = description;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "description_id")
    public int getDescriptionId() {

        return descriptionId;
    }

    public void setDescriptionId(int descriptionId) {
        this.descriptionId = descriptionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
