import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.stream.Collectors;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.out.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        // Close caches and connection pools
        getSessionFactory().close();
    }

    static MessageCode addNewWord(Session session, Message message){
        Word word = new Word();
        Description description = new Description();

        if (message.getStringList().size() > 1) {
            description.setDescription(message.getStringList().get(1));
        }
        session.save(description);
        
        word.setWord(message.getStringList().get(0));
        word.setDescription(description);
        session.save(word);

        return MessageCode.OK;
    }

    static Description fetchDescriptionByWord(Session session, Message message){
        Word word = (Word) session.createCriteria(Word.class)
                .add(Restrictions.eq("word",message.getStringList().get(0))).uniqueResult();
        return word.getDescription();
    }

    static List<String> fetchWordsByMask(Session session, Message message){

        List<Word> words = session.createCriteria(Word.class).list();

        return   words.stream()
                .filter(el -> HibernateUtil.isMatch(el.getWord(), message.getStringList().get(0)))
                .map(Word::getWord)
                .collect(Collectors.toList());

    }

    static MessageCode updateWord(Session session, Message message){
        Word word = (Word) session.createCriteria(Word.class)
                .add(Restrictions.eq("word",message.getStringList().get(0))).uniqueResult();

        if (word == null)
            return MessageCode.WORD_NOT_FOUND;

        word.setWord(message.getStringList().get(1));
        word.getDescription().setDescription(message.getStringList().get(2));

        session.update(word);

        return MessageCode.OK;
    }

    static MessageCode deleteWord(Session session, Message message){
        Word word = (Word) session.createCriteria(Word.class)
                .add(Restrictions.eq("word",message.getStringList().get(0))).uniqueResult();

        if (word == null)
            return MessageCode.WORD_NOT_FOUND;

        session.delete(word);
        return MessageCode.OK;
    }


    // mask filter
    private static boolean isMatch(String s, String p) {
        int i = 0;
        int j = 0;
        int starIndex = -1;
        int iIndex = -1;

        while (i < s.length()) {
            if (j < p.length() && (p.charAt(j) == '?' || p.charAt(j) == s.charAt(i))) {
                ++i;
                ++j;
            } else if (j < p.length() && p.charAt(j) == '*') {
                starIndex = j;
                iIndex = i;
                j++;
            } else if (starIndex != -1) {
                j = starIndex + 1;
                i = iIndex+1;
                iIndex++;
            } else {
                return false;
            }
        }
        while (j < p.length() && p.charAt(j) == '*') {
            ++j;
        }
        return j == p.length();
    }

}