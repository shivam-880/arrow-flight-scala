# arrow-flight-scala

Scala version of the Java Arrow Flight [example](https://arrow.apache.org/cookbook/java/flight.html).

This project is different from the Java example in the following ways:
1. Reader and Writer are clients for respective purposes running in different threads
2. Server is started in a separate thread as well
3. BlockingQueue is used in "Producer" to allow writing and reading every batch one after the another instead of building batch of batches as implemented in the Java example

**TODO**: Close resources properly
