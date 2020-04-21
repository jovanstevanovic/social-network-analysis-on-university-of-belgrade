import java.util.HashMap;

class SharedData {

    // Authors hashmaps.
    static HashMap<String, Author.EntryAuthor> hashMapAllAuthors = new HashMap<>();
    static HashMap<String, Author.EntryAuthor> hashMapETFAuthors = new HashMap<>();
    static HashMap<String, Author.EntryAuthor> hashMapMATFAuthors = new HashMap<>();
    static HashMap<String, Author.EntryAuthor> hashMapFONSIAuthors = new HashMap<>();
    static HashMap<String, Author.EntryAuthor> hashMapFONISAuthors = new HashMap<>();
    static HashMap<String, Author.EntryAuthor> hashMapFONITAuthors = new HashMap<>();

    // Publication hashmaps.
    static HashMap<String, Publication.EntryPublication> hashMapAllPublications = new HashMap<>();
    static HashMap<String, Publication.EntryPublication> hashMapNonConferencePublications = new HashMap<>();
    static HashMap<String, Publication.EntryPublication> hashMapConferencePublications = new HashMap<>();
}
