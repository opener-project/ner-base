package es.ehu.si.ixa.pipe.nerc;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import opennlp.tools.util.Span;

import org.junit.BeforeClass;
import org.junit.Test;

import eus.ixa.ixa.pipe.nerc.DictionariesNameFinder;
import eus.ixa.ixa.pipe.nerc.dict.Dictionaries;

public class DictionariesNameFinderTest {

    private static DictionariesNameFinder finder = null;
    
    @BeforeClass
    public static void setUpClass() throws IOException {
        // copy to a temporary dir so that it can be loaded
        File dictsDir = Files.createTempDirectory("dicts").toFile();
        Files.copy(DictionariesNameFinderTest.class
                .getResourceAsStream("/names.txt"),
                new File(dictsDir, "names.txt").toPath());
        // now load it into a Dictionaries instance
        finder = new DictionariesNameFinder(
                new Dictionaries(dictsDir.getAbsolutePath()));
    }
    
    @Test
    public void oneOccurrence() throws IOException {
        Span[] spans = finder.nercToSpansExact(new String[] {"Achilles"});
        assertEquals(1, spans.length);
    }

    @Test
    public void twoOccurrences() throws IOException {
        Span[] spans = finder.nercToSpansExact(new String[] {
                "Achilles", "Apollo", "Zeus", "Achilles"});
        assertEquals(2, spans.length);
    }

}
