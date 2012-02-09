#!/usr/bin/python

import sys
import json
from pprint import pprint

if len(sys.argv) != 3:
    print "use like this: ./convert.py input_file output_file"
    exit(1)
    
f = open(sys.argv[1], 'r')
m = open(sys.argv[1][:-3] + "mtl", 'r')

def parse_xyz(str):
    arr = l[2:].strip().split(' ')
    return {'x':float(arr[0]), 'y':float(arr[1]), 'z':float(arr[2])}

materials = []
for l in m:
    if l.startswith('newmtl'):
        material = {'name' : l[7:-1], 'alpha': 1, 'scale': 1.0, 'img': l[7:-1] + ".jpg" , 'ofsetX': 0, 'ofsetY': 0 }
        for l1 in m:
            if l1[0] == 'd':
                material['alpha'] = float(l1[2:])
                break
        materials.append(material)
materials = sorted(materials, key=lambda mat: mat['alpha'], reverse=True)

obj = {'name': "", 'vertices': [], 'normals': [], 'faces':[],'materials': materials}
output = ""
material = ""
material_index = 0
for l in f:
    
    if l[0] == 'u':
        material = l[7:-1]
        for i in range(len(materials)):
            if material == materials[i]['name']:
                material_index = i       
            
    
    if l[0] == 'o':
        obj["name"] = l[2:].strip()
        
    if l[0] == 'v':
        obj['vertices'].append(parse_xyz(l[2:]))
        
    if l[0:2] == 'vn':
        obj['normals'].append(parse_xyz(l[3:]))
        
    if l[0:1] == 'f':
        face = {'type':0, 'material': "", 'vertices': [], 'normals':[]}
        arr = l[2:].strip().split(' ')  
        face["type"] = len(arr)
        
        face["material"] = material_index
        
        for i in arr:
            n = i.split('//')
            face["vertices"].append(int(n[0])-1)
            
            if len(n) == 2:
                face["normals"].append(int(int(n[1])-1))
               
        obj["faces"].append(face)
        
    if l[0] == 'n':
        output += ""     

# Put those with highest alpha to the top:
#obj['faces'] = sorted(obj['faces'], key=lambda face: obj['materials'][face['material']]['alpha'], reverse=True)

# Return json
json.dump(obj, open(sys.argv[2], 'w')), indent=4)

# Prepend object name
import fileinput
for n,line in enumerate(fileinput.FileInput(sys.argv[2],inplace=1)):
    if n == 0: print sys.argv[2][:-3] + " = "
    print line

print "Done"
