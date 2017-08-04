import csv
import random
import copy

def write_to_file(data_list):
    result = ""
    for i in range(1,len(data_list)):
        slope = data_list[i] - data_list[i-1]
 
        if slope > 0:
            result += "/up"
        if slope == 0:
            result += "/stable"
        if slope  < 0:
            result += "/down"

    result += "\n"      
    return result[1:]           
def main():
    f = open("figures_and_sdl.txt","w")
    i = 0   

    csvfile = open("data.csv","wb")
    spamwriter = csv.writer(csvfile, delimiter=',',quotechar=',', quoting=csv.QUOTE_MINIMAL)
    spamwriter.writerow(['z','x','y'])
    for segments_length in range(3,21):
        for times in range(31):
            slope=[-1,0,1]
            latest_y = 0
            x = []
            y = []
            for s in range(0,segments_length):
                              
                index = random.randint(0,len(slope)-1)
                x.append(s)
                y.append(latest_y+slope[index])
                latest_y = latest_y+slope[index]
                    
                if min(y) < 0:
                    _m = min(y)
                    for j in range(len(y)):
                        y[j] = -_m + y[j]       
                
            for k in range(len(y)):
                spamwriter.writerow([str(i),x[k],y[k]]) 

            f.write(str(i)+"/"+write_to_file(y))
            
            i += 1
            print segments_length, 
            print y
        
    csvfile.close()        
    f.close()            
main()