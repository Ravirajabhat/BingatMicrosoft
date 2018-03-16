#from sklearn.feature_extraction.text import TfidfVectorizer
#tf = TfidfVectorizer(analyzer='word', min_df = 0)

def arffCreator(sourcefileName,arffFilename):
    body="@RELATION book\n@ATTRIBUTE publicationYear NUMERIC\n"

    autherCountMap={}
    wordCountMap={}
    documentList=[]
    document={}
    descriptiveContentList=[]

    with open(sourcefileName,'r') as f:
        for line in f:
            data = line.split('\t')
            for auther in data[3].split(';'):
                if auther not in autherCountMap:
                    autherCountMap[auther] = 1
                else:
                    autherCountMap[auther] += 1
            
            descriptiveContent=data[4]+". "+data[5]
            for word in descriptiveContent.split(' '):
                word=word.replace(".","")
                word=word.replace("\n","")
                if word not in wordCountMap:
                    wordCountMap[word] = 1
                else:
                    autherCountMap[auther] += 1
            document['recordId']=data[1]
            document['publicationYear']=data[2]
            document['descriptiveContent']=descriptiveContent
            document['authers']=data[3].split(';')
            documentList.append(document)
            descriptiveContentList.append(descriptiveContent)

    for key in autherCountMap:
        key="author="+key.replace(' ','_')
        body+="@ATTRIBUTE "+key+" NUMERIC\n"
    for key in wordCountMap:
        body+="@ATTRIBUTE "+key+" NUMERIC\n"
    body+='\n@DATA\n'

    arffFile = open(arffFilename, "w")
    arffFile.write(body)
    for document in documentList:
        trainContent=""
        if arffFilename == 'book-test.arff':
            trainContent="?"
        else:
            trainContent=document['publicationYear']
        for key, value in autherCountMap.items():
            if key not in document['authers']:
                trainContent+=","+str(value)
            else:
                trainContent+=",0"
        for key, value in wordCountMap.items():
            if key in document['descriptiveContent']:
                trainContent+=","+str(value)
            else:
                trainContent+=",0"
        arffFile.write(trainContent+"\n")
    arffFile.close()
    
arffCreator('BingHackathonTrainingData.txt','book-train.arff')
arffCreator('BingHackathonTestData.txt','book-test.arff')
