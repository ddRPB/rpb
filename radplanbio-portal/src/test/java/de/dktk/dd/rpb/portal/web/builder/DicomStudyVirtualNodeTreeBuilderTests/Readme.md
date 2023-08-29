# DicomStudyVirtualNodeTreeBuilder Tests

The DicomStudyVirtualNodeTreeBuilder tests are organized in different test classes, 
and a helper class (DicomStudyVirtualNodeTreeBuilderTestHelper) in order to keep it easy to read and
maintain the tests. It also reflects the two different contexts of usage. 


## Basic Test (DicomStudyVirtualNodeTreeBuilderBasicTest)

In the first phase DICOM data are fetched that do not consist of the DICOM images to keep the load process fast.
Such series are all flat on the root node of the tree.

## With Images Test (DicomStudyVirtualNodeTreeBuilderWithImagesTest)

In a second phase, DICOM images will be loaded for series that are RT related. These images refer to other DICOM series.
Based on that references, we are creating a tree with the hierarchy : CT -> RTStruct -> RTPlan -> RTImage/RTDose.
I case that a parent element is missing, the branch or single node is added to the root node as it is. 

