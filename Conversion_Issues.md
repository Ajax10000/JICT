Conversion Issues

Typically after allocating memory for an array or pointer, the original C++ code will check to see if the pointer or array is null. An example snippet taken from the shape3d constructor in SHAPE3D.CPP is shown below:

```cpp
    void *nullPointer = calloc(numVertices, sizeof(vertexSet));
    if(nullPointer == 0) {
        statusPrint("shape3d::shape3d: Unable to allocate shape object");
        numAllocatedVertices = 0; //signal an error
    }
```

That is not usually done in Java code, though you can check for an OutOfMemoyException. But I think that most programmers would agree that an out-of-memory exception is not recoverable.

Typically after instantiating an object, the original C++ code will call the object's isValid() method to determine if the constructor succeeded.
