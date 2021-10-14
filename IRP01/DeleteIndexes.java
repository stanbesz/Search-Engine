import java.io.File;

public class DeleteIndexes {
    public static void deleteIndexesFolder(String pathToIndexes) {
        File folder = new File(pathToIndexes);
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteIndexesFolder(f.getPath());
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
        System.out.println();
        System.err.println("The folder with the indexes has been deleted!");
    }
}