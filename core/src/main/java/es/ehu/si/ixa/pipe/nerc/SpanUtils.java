package es.ehu.si.ixa.pipe.nerc;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.Span;

public class SpanUtils {
  
  /**
   * Concatenates two span lists adding the spans of the second parameter to the
   * list in first parameter.
   *
   * @param allSpans
   *          the spans to which the other spans are added
   * @param neSpans
   *          the spans to be added to allSpans
   */
  public static final void concatenateSpans(final List<Span> allSpans,
      final List<Span> neSpans) {
    for (Span span : neSpans) {
      allSpans.add(span);
    }
  }

  /**
   * Removes spans from the preList if the span is contained in the postList.
   *
   * @param preList
   *          the list of spans to be post-processed
   * @param postList
   *          the list of spans to do the post-processing
   */
  public static final void postProcessDuplicatedSpans(final List<Span> preList,
      final List<Span> postList) {
    List<Span> duplicatedSpans = new ArrayList<Span>();
    for (Span span1 : preList) {
      for (Span span2 : postList) {
        if (span1.contains(span2)) {
          duplicatedSpans.add(span1);
        } else if (span2.contains(span1)) {
          duplicatedSpans.add(span1);
        }
      }
    }
    preList.removeAll(duplicatedSpans);
  }

}
