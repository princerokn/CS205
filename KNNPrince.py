import math
import re
import operator
import time
import decimal
def nearestNeighbor(train,test,current):
    Distances = []
    for t in train:
        distance = 0
        for i in current:
            distance += math.pow(t[i] - test[i], 2)
        d = math.sqrt(distance)
        Distances.append([d,t[0]])
    Distances.sort(key=operator.itemgetter(0))
    return  Distances[0][1]
def leaveOneOutCrossValidaation(graph,currFeature,feature,algo):
    cur = []
    count = 0
    for i in currFeature:
        cur.append(i)
    if algo == 1:
        cur.append(feature)
    else:
        cur.remove(feature)
    for j in range(0, len(graph)):
        train = graph[:]
        test = train.pop(j)
        gr = nearestNeighbor(train,test,cur)
        if test[0] == gr:
            count += 1
    accuracy = count / float(len(graph))
    return accuracy
def forwardSelection(graph):
    featureNo = len(graph[0])
    accuray = []
    finalFeatures = []
    finalAccuracy = 0
    currFeature = []
    print ("Beginning Search.")
    for i in range(1,featureNo):
        bestAcc = 0
        featureToAdd = -1
        for j in range(1,featureNo):
            if j not in currFeature:
                accuracy = leaveOneOutCrossValidaation(graph,currFeature,j,1)
                print ("     Using feature(s) {"+str(j)+"} accuracy is "+str(round((accuracy * 100),2))+"%")
                if accuracy > bestAcc:
                    bestAcc = accuracy
                    featureToAdd = j
        if featureToAdd != -1:
            currFeature.append(featureToAdd)
            print ("Feature set "+ str(currFeature)+", was best, accuracy is :"+str(round((bestAcc * 100),2))+"%")
            accuray.append(bestAcc)
        if bestAcc > finalAccuracy:
            finalFeatures = currFeature[:]
            finalAccuracy = bestAcc
    print ("Finished Search!! Best feature subset is: "+str(finalFeatures)+", which has an accuracy of "+str(round((finalAccuracy*100),2))+"%")
def backwardElimination(graph):
    finalFeatures = []
    finalAccuracy = 0
    currFeature = []
    for i in range (1,len(graph[0])):
        currFeature.append(i)
    featureNo = len(graph[0])
    accuray = []
    print ("Beginning Search.")
    for i in range(1, featureNo):
        featureToRemove = -1
        bestAcc = 0
        for j in range(1, featureNo):
            if j in currFeature:
                accuracy = leaveOneOutCrossValidaation(graph, currFeature,j,2)
                print ("     Using feature(s) {"+str(j)+"} accuracy is "+str(round((accuracy * 100),2))+"%")
                if accuracy > bestAcc:
                    bestAcc = accuracy
                    featureToRemove = j
        if featureToRemove != -1:
            currFeature.remove(featureToRemove)
            print("Feature set " + str(currFeature) + ", was best, accuracy is :" + str(round((bestAcc * 100), 2)) + "%")
            accuray.append(bestAcc)
        if bestAcc > finalAccuracy:
            finalFeatures = currFeature[:]
            finalAccuracy = bestAcc
    print ("Finished Search!! Best feature subset is: "+str(finalFeatures)+", which has an accuracy of "+str(round((finalAccuracy*100),2))+"%")
def dataNormalization(graph):
	dataSet = graph
	avg = [0.00]*(len(dataSet[0])-1)
	sd = [0.00]*(len(dataSet[0])-1)
	for i in dataSet:
		for j in range (1,(len(i))):
			avg[j-1] +=  i[j]
	for i in range(len(avg)):
		avg[i] = (avg[i]/len(dataSet))
	for i in dataSet:
		for j in range (1,(len(i))):
			sd[j-1] +=  pow((i[j] - avg[j-1]),2)
	for i in range(len(sd)):
		sd[i] = math.sqrt(sd[i]/len(dataSet))
	for i in range(len(dataSet)):
		for j in range (1,(len(dataSet[0]))):
			dataSet[i][j] = (dataSet[i][j] - avg[j-1])/sd[j-1]
	return dataSet
if __name__ == '__main__':
    print("Welcome to Prince's Feature Selection Algorithm")
    testfile = input("Type the file name to test:")
    with open(testfile) as file:
        dataset = file.readlines()
    rows = []
    graph = []
    dataset = [i.strip() for i in dataset]
    for line in dataset:
        values = re.split(" +",line)
        for val in values:
            value = float(decimal.Decimal(val))
            rows.append(value)
        graph.append(rows)
        rows = []
    print ("Type the number corresponding to algorithm to use it: \n1)Forward Selection \n2)Backward elimination")
    n = int(input("Enter choice here: "))
    print ("This dataset has "+str(len(graph[0])-1)+" features(not including class attributes), with "+str(len(graph))+" instances.")
    print ("Normalizing dataset")
    graph = dataNormalization(graph)
    print("Normalization done!")
    start_time = time.time()
    if n==1:
        forwardSelection(graph)
    elif n==2:
        backwardElimination(graph)
    else:
        print("You have entered wrong choice")
    print("Total time taken %s" % (time.time() - start_time))