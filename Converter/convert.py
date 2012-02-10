#!/usr/bin/python

import sys
import json
from pprint import pprint

if len(sys.argv) != 3:
    print "use like this: ./convert.py class_name"
    exit(1)
    
f = open(sys.argv[1], 'r')
m = open(sys.argv[1][:-3] + "mtl", 'r')

def parse_xyz(str):
    arr = l[2:].strip().split(' ')
    return [float(arr[0]), float(arr[1]), float(arr[2])]

materials = []
for l in m:
    if l.startswith('newmtl'):
        material = {'name' : l[7:-1], 'alpha': 1, 'color': [], 'scale': 1.0, 'img': l[7:-1] + ".jpg" , 'ofsetX': 0, 'ofsetY': 0 }
        for l1 in m:
            if l1[:2] == 'Kd':
                material['color'] = l1[3:-1].split(' ')
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
        
    if l[0:2] == 'v ':
        obj['vertices'].append(parse_xyz(l[2:]))
        
    if l[0:2] == 'vn':
        obj['normals'].append(parse_xyz(l[3:]))
        
    if l[0:1] == 'f':
        face = {'type':0, 'material': "", 'vertices': [], 'normals':[]}
        arr = l[2:].strip().split(' ')  
        face["type"] = len(arr)
        
        face["material"] = material_index
        for i in arr:
            n = i.split('/')
            face["vertices"].append(int(n[0])-1)
            
            if len(n) == 2:
                face["normals"].append(int(int(n[1])-1))
               
        obj["faces"].append(face)
        
    if l[0] == 'n':
        output += ""

cl = "package org.psywerx.car.gen;\n\npublic class "+ sys.argv[2] +"{\n"  
arr = "\t public static float[][] v = {"
clr = "\t public static float[][] c = {"
for m in xrange(len(obj['materials'])):
    arr += '{'
    for f in obj['faces']:
        if f['material'] != m:
            continue
        
        for v in f['vertices']:
            for p in obj['vertices'][v]:
                arr += str(p) + 'f,'
        arr += "\n\t\t"
    arr += '},'

    clr += "{"
    for c in obj['materials'][m]['color']:
        clr += c + "f,"
    clr += "},\n\t\t"
clr += '};'
arr += '};'
cl += arr + "\n"
cl += clr
cl += "\n}"

f = open('../galaxyCar/src/org/psywerx/car/gen/' + sys.argv[2] + '.java', 'w')
f.write(cl)
f.close()

print "Done"
