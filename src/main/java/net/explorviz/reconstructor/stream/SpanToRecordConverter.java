package net.explorviz.reconstructor.stream;

import java.time.Instant;
import java.util.Arrays;
import javax.enterprise.context.ApplicationScoped;
import net.explorviz.avro.landscape.flat.Application;
import net.explorviz.avro.landscape.flat.LandscapeRecord;
import net.explorviz.avro.landscape.flat.Node;
import net.explorviz.avro.EVSpan;

/**
 * Maps {@link EVSpan} objects to {@link LandscapeRecord}
 * objects by extracting the data.
 */
@ApplicationScoped
public class SpanToRecordConverter {


  /**
   * Converts a {@link EVSpan} to a {@link LandscapeRecord} using the structural information
   * given in the span.
   *
   * @param span the span
   * @return a records containing the structural information of the span
   */
  public LandscapeRecord toRecord(EVSpan span) {

    // Create new builder
    LandscapeRecord.Builder recordBuilder =
        LandscapeRecord.newBuilder().setLandscapeToken(span.getLandscapeToken());

    // Use start time as timestamp
    Instant timestamp = Instant
        .ofEpochSecond(span.getStartTime().getSeconds(), span.getStartTime().getNanoAdjust());
    recordBuilder.setTimestamp(timestamp.toEpochMilli());

    // Set node and application
    recordBuilder.setTimestamp(timestamp.toEpochMilli())
        .setNode(new Node(span.getHostIpAddress(), span.getHostname()))
        .setApplication(
            new Application(span.getAppName(), span.getAppPid(), span.getAppLanguage()));


    /*
      By definition getOperationName().split("."):
        Last entry is method name,
        next to last is class name,
        remaining elements form the package name
    */
    String[] operationFqnSplit = span.getOperationName().split("\\.");


    String pkgName =
        String.join(".", Arrays.copyOf(operationFqnSplit, operationFqnSplit.length - 2));
    String className = operationFqnSplit[operationFqnSplit.length - 2];
    String methodName = operationFqnSplit[operationFqnSplit.length - 1];


    recordBuilder.setPackage$(pkgName);
    recordBuilder.setClass$(className);
    recordBuilder.setMethod(methodName);


    return recordBuilder.build();

  }



}