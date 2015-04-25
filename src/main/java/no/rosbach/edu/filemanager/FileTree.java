package no.rosbach.edu.filemanager;

import javax.tools.JavaFileObject;
import java.util.Collection;

/**
 * Created by mapster on 15.03.15.
 */
public interface FileTree<T extends JavaFileObject> {
    String PATH_SEPARATOR = "/";

    public void add(T file);
    public Collection<T> listFiles(String path, boolean recurse);


    enum PathSeparator {
        FILESYSTEM(PATH_SEPARATOR), PACKAGE(".");

        private final String separator;

        private PathSeparator(String separator) {
            this.separator = separator;
        }

        @Override
        public String toString() {
            return separator;
        }
    }
}
