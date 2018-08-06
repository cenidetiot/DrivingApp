import math
import time

lastPoint = None

def distance(start, end):
    rlat0 = math.radians(start[0])
    rlng0 = math.radians(start[1])
    rlat1 = math.radians(end[0])
    rlng1 = math.radians(end[1])
    latDelta = rlat1 - rlat0
    lonDelta = rlng1 - rlng0
    distance = 6371000 * 2 * math.asin(
        math.sqrt(
            math.cos(rlat0) * math.cos(rlat1) * math.pow(math.sin(lonDelta / 2), 2) +
            math.pow(math.sin(latDelta / 2), 2)
        )
    )
    return distance 

def wrongWay(currentPoint,lastPoint,startPoint, endPoint):
    if (lastPoint == None):
        lastPoint = currentPoint
    startToLastDistance = distance(startPoint, lastPoint)
    startToCurrentDistance = distance(startPoint, currentPoint)
    endToLastDistance = distance(endPoint, lastPoint)
    endToCurrentDistance = distance(endPoint, currentPoint)
    
    if ( not (startToCurrentDistance >= startToLastDistance and endToLastDistance >= endToCurrentDistance)) :
        print ("WRONG", startToCurrentDistance , startToLastDistance, endToLastDistance, endToCurrentDistance)
    else: 
        print ("NOT", startToCurrentDistance , startToLastDistance, endToLastDistance, endToCurrentDistance)
    return currentPoint

segment = [[18.879618, -99.221824],[18.879829, -99.221514]]
startPoint = [18.879883, -99.221575]
endPoint = [18.879715, -99.221754]

totalDistance = 0
startToLastDistance = 0 
startToCurrentDistance = 0
endToLastDistance = 0 
endToCurrentDistance = 0;

totalDistance = distance(startPoint, endPoint)
print(totalDistance)

recorrido = [
    [ 18.879883, -99.221575],
    [18.879855, -99.221599],
    [18.87981, -99.221653],
    [18.879765, -99.2217],
    [18.879715,-99.221754]
]

print("POR el buen camino")
for item in recorrido :
    lastPoint = wrongWay(item, lastPoint, startPoint, endPoint)
    time.sleep(1)

print("POR el mal camino")
lastPoint = None
for item in reversed(recorrido) :
    lastPoint = wrongWay(item, lastPoint, startPoint, endPoint)
    time.sleep(1)




