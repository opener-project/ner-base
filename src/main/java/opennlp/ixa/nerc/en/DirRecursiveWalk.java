/**
 * 
 */
package opennlp.ixa.nerc.en;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.io.FilenameUtils;

/**
 * @author ragerri
 * 
 */
public class DirRecursiveWalk extends SimpleFileVisitor<Path> {

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
      throws IOException {
    
    if (FilenameUtils.isExtension(file.toString(), "txt")) {
      Path outfile = Paths.get(FilenameUtils.removeExtension(file.toString()) + ".xml");
      Annotate.nerc2kaf(file, outfile);
      System.out.println();
      System.out.format("Processing file: %s ", file);
      System.out.println();
      System.out.println(">> SUCCESS!! Wrote KAF annotation to " + outfile.toString());
    }
    else {
      System.out.format(
          ">> FAILED!! Extension of %s is not txt. Is this a plain text file? ", file);
      System.out.println();
    }
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFileFailed(Path file,
                                     IOException exc) {
      System.err.println(exc);
      return FileVisitResult.CONTINUE;
  }
  
  
}
