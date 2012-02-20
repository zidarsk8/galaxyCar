#!/usr/bin/python

import os
import sys
import json
from pprint import pprint

def convert(fileName):
		
	f = open(fileName+".obj", 'r')
	m = open(fileName+".mtl", 'r')

	def parse_xyz(s):
		arr = s.strip().split(' ')
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
				
				if len(n) == 3:
					face["normals"].append(int(int(n[2])-1))
				   
			obj["faces"].append(face)
			
		if l[0] == 'n':
			output += ""

	arr = ""
	clr = ""
	nor = ""
	for m in range(len(obj['materials'])):
		for f in obj['faces']:
			if f['material'] != m:
				continue
			
			for v in f['vertices']:
				for p in obj['vertices'][v]:
					arr += str(p) + ','
			for n in f['normals']:
				for k in obj['normals'][n]:
					nor += str(k) + ','
		arr = arr[:-1] + "\n"
		nor = nor[:-1] + "\n"

		for c in obj['materials'][m]['color']:
			clr += c + ","
		
		clr = clr[:-1] + "\n"
	print("writing: "+fileName+".v.csv")
	f = open('../galaxyCar/assets/' + fileName + '.v.csv', 'w')
	f.write(arr)
	f.close()
	
	
	print("writing: "+fileName+".c.csv")
	f = open('../galaxyCar/assets/' + fileName + '.c.csv', 'w')
	f.write(clr)
	f.close()
	
	
	print("writing: "+fileName+".n.csv")
	f = open('../galaxyCar/assets/' + fileName + '.n.csv', 'w')
	f.write(nor)
	f.close()

	print("writing: "+fileName+".v.csv")
	f = open('../galaxyPhone/assets/' + fileName + '.v.csv', 'w')
	f.write(arr)
	f.close()
	
	
	print("writing: "+fileName+".c.csv")
	f = open('../galaxyPhone/assets/' + fileName + '.c.csv', 'w')
	f.write(clr)
	f.close()
	
	
	print("writing: "+fileName+".n.csv")
	f = open('../galaxyPhone/assets/' + fileName + '.n.csv', 'w')
	f.write(nor)
	f.close()



for ar in range(1,len(sys.argv)):
	fname = sys.argv[ar]
	if fname.find("*") == len(fname)-1:
		dirList = os.listdir(".")
		for dirFiles in dirList:
			if dirFiles.find(fname[:-1]) == 0 and dirFiles[-4] == ".obj":
				convert(dirFiles[:-4])
	elif fname.find(".obj") != -1:
		convert(fname[:-4])
	elif fname.find(".") == -1:
		convert(fname)

print("Done")
