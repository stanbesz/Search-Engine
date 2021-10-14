public class Main {

    public static void main(String[] args) {
        String pathToFolder = null;
        try {
            pathToFolder = args[0];
        } catch (Exception ex) {
            System.out.println("Path argument can't be empty! Please try again.");
            System.exit(-1);
        }
        System.err.println("Make sure that the path you have entered doesn't contain a directory named 'Indexes', " +
                "because it is going to be deleted after the end of the program!");
        System.out.println("Folder to be traversed: " + pathToFolder);
        System.out.println();
        Index indexer = null;
        String indexLocation = null;
        try {
            indexLocation = pathToFolder + "\\Indexes";
            indexer = new Index(indexLocation);
        } catch (Exception ex) {
            System.out.println("Cannot create index..." + ex.getMessage());
            System.exit(-1);
        }
        try {
            indexer.indexFileOrDirectory(pathToFolder);
            indexer.closeIndex();
        } catch (Exception e) {
            System.out.println("Error indexing " + pathToFolder + " : " + e.getMessage());
        }
        Search.search(indexLocation);
        DeleteIndexes.deleteIndexesFolder(indexLocation);
    }

}
