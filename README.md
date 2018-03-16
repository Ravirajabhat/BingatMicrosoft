
Challenge1:
Predicting topic of the book

Instruction to run Code:

1.set up app.properties keys
2.run OpenNLPMaxentTopicClassifier.java in com.teamai.hackathon.impl package.


Description:

We have built 3 distinct topic classifier by using Open NLP document categorizer (maxent algorithm) .

Technical details:
Prepare custome train file for DocumentCategorizer by considering author , titel and summary fields.
Build and evaluate the maxcent classifier using cross-fold ealuation.
Supply test data on to the classifier.
Cross-fold Validation result:(80:20)
TopicCategory0 Classifier : Accuracy:0.8333333333333334
TopicCategory1 Classifier : Accuracy:0.7188888888888889
TopicCategory2 Classifier : Accuracy:0.8155555555555556
# BingatMicrosoft
