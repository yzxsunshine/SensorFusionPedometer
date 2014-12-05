package com.xyzstudio.badlogic.framework.mesh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.xyzstudio.badlogic.framework.FileIO;
import com.xyzstudio.badlogic.framework.gl.Model;
import com.xyzstudio.badlogic.framework.gl.Texture;
import com.xyzstudio.badlogic.framework.gl.Vertices3;
import com.xyzstudio.badlogic.framework.impl.GLGame;
import com.xyzstudio.badlogic.framework.impl.GLGraphics;


public class PlyMeshIO implements MeshIO {

	protected static final int FIELD_NUM = 6;
	protected static final int DATATYPE_NUM = 6;
	
	enum plyByteType
	{
		BIG_ENDIAN,
		LITTLE_ENDIAN,
		UNDEFINED
	};

	enum plyStorageType
	{
		ASCII,
		BINARY
	};

	enum plyDataType
	{
		NONE,
		UCHAR,
		USHORT,
		INT,
		FLOAT,
		DOUBLE
	};
	
	enum plyFieldType
	{
		COORD(0),
		CONF(1),
		COLOR(2),
		UV(3),
		NORM(4),
		INTENSITY(5);
		
		private int value = 0;
		private plyFieldType(int val)
		{
			this.value = val;
		}
		
		public static plyFieldType valueOf(int val)
		{
			switch (val)
			{
				case 0:
				{
					return plyFieldType.COORD;
				}
				case 1:
				{
					return plyFieldType.CONF;
				}
				case 2:
				{
					return plyFieldType.COLOR;
				}
				case 3:
				{
					return plyFieldType.UV;
				}
				case 4:
				{
					return plyFieldType.NORM;
				}
				case 5:
				{
					return plyFieldType.INTENSITY;
				}
				default:
				{
					return null;
				}
			}
		}
		
		public int value()
		{
			return this.value;
		}
	};
	
	class PlyHeader {
		public int nVerts = 0;	// Number of vertex
		public int nFaces = 0;	// Number of faces
		public int nColorChannal = 0;
		public plyByteType byteFmt;	// file format: big endian, little endian, undefined
		public plyStorageType storageFmt;	// storage format: binary, ascii
		public plyDataType fields[] = new plyDataType[FIELD_NUM];	//fields recorded about vertexes
		public int fieldIds[] = new int[FIELD_NUM];
		public int nFields = 0;
		public plyDataType faceVertNumType;
		public plyDataType faceIdxType;
		public boolean isFaceUV;
		public String texturePath;	// WARNING! only support one texture
		//int textureNum;
		
		public PlyHeader() {
			for(int i=0; i<FIELD_NUM; i++)
			{
				fields[i] = plyDataType.NONE;
				fieldIds[i] = -1;
			}
		}
	}
	
	@Override
	public Model readMesh(String fileName, String texName, GLGraphics glGraphics, GLGame glGame) {
		return this.readMesh(fileName, texName, glGraphics, glGame.getFileIO());
	}
	
	@Override
	public Model readMesh(String fileName, String texName,
			GLGraphics glGraphics, FileIO fio) {
		// TODO Auto-generated method stub
		Model model = new Model(glGraphics);
		FileIO fileIO = fio;
		InputStream in = null;
		if(texName != null)
		{
			model.texture = new Texture(glGraphics, fileIO, texName);
		}
		try {
			in = fileIO.readAsset(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			PlyHeader plyHeader = readHeader(reader);
			if(plyHeader != null)
			{
				boolean hasNormal = false;
				boolean hasColor = false;
				boolean hasUV = false;
				if(plyHeader.fieldIds[plyFieldType.COLOR.value()] >= 0)
				{
					hasColor = true;
				}
				if(plyHeader.fieldIds[plyFieldType.UV.value()] >= 0)
				{
					hasUV = true;
					if(plyHeader.texturePath != null && texName == null)
					{
						model.texture = new Texture(glGraphics, fileIO, plyHeader.texturePath);
					}
				}
				if(plyHeader.fieldIds[plyFieldType.NORM.value()] >= 0)
				{
					hasNormal = true;
				}
				model.vertices = new Vertices3(glGraphics, plyHeader.nFaces*3, 0, hasColor, hasUV, hasNormal);
				if(plyHeader.storageFmt == plyStorageType.ASCII)
				{
					if(!plyReadAscii(reader, plyHeader, model))
					{
						System.out.println("Read Ply Model Failed");
					}
				}
				else
				{
					
				}
			}
		}
		catch (IOException e) {
			throw new RuntimeException("Couldn't load ply model '" + fileName
					+ "'", e);
		} finally {
			if (in != null)
			{
				try {
					
					in.close();
				} 
				catch (IOException e) {
				}
			}
		}
		return model;
	}
	
	private PlyHeader readHeader(BufferedReader reader)
	{
		PlyHeader plyHeader = new PlyHeader();
		byte[] buf = new byte[256];
        String line = null;
        try {
        	String pattern = new String();
        	line = reader.readLine();
        	if(!line.equalsIgnoreCase("ply"))
            {
            	System.out.println("Invalid input ply file");
            }
        	
        	// read data format, binary or ascii, big or little endian
            while ((line = reader.readLine()) != null) {
                if(line.contains("format"))
                {
                	break;
                }
            }
            if(line == null)
            {
            	return null;
            }
            if(line.contains("binary_big_endian"))
            {
            	plyHeader.byteFmt = plyByteType.BIG_ENDIAN;
            	plyHeader.storageFmt = plyStorageType.BINARY;
            }
            else if(line.contains("binary_little_endian"))
            {
            	plyHeader.byteFmt = plyByteType.LITTLE_ENDIAN;
            	plyHeader.storageFmt = plyStorageType.BINARY;
            }
            else if(line.contains("ascii"))
            {
            	plyHeader.byteFmt = plyByteType.UNDEFINED;
            	plyHeader.storageFmt = plyStorageType.ASCII;
            }
            else
            {
            	System.out.println("Unknown file format");
            	return null;
            }
            
            
            while ((line = reader.readLine()) != null) {
            	pattern = "comment TextureFile";
                if(line.contains(pattern))
                {
                	plyHeader.texturePath = line.substring(pattern.length() + 1);	//+1 because there's a blank
                	continue;
                }
                else if(line.contains("element"))
                {
                	break;
                }
            }
            
            // read vertex properties;
            pattern = "element vertex";
            if(line.contains(pattern))
            {
            	plyHeader.nVerts = Integer.parseInt(line.substring(pattern.length()+1));
            }
            
            pattern = "property";
            plyDataType fmt;
            do {
            	line = reader.readLine();
            	if(line.contains("element face"))
        		{
        			break;
        		}
            	String[] tokens = line.split(" ");
            	if(tokens.length < 3)
            		break;
            	// data type
            	if(tokens[1].equalsIgnoreCase("uchar") || tokens[1].equalsIgnoreCase("uint8"))
            	{
            		fmt = plyDataType.UCHAR;
            	}
            	else if(tokens[1].equalsIgnoreCase("uint16") || tokens[1].equalsIgnoreCase("ushort"))
            	{
            		fmt = plyDataType.USHORT;
            	}
            	else if(tokens[1].equalsIgnoreCase("int") || tokens[1].equalsIgnoreCase("int32"))
            	{
            		fmt = plyDataType.INT;
            	}
            	else if(tokens[1].equalsIgnoreCase("float"))
            	{
            		fmt = plyDataType.FLOAT;
            	}
            	else if(tokens[1].equalsIgnoreCase("float64") || tokens[1].equalsIgnoreCase("double"))
            	{
            		fmt = plyDataType.DOUBLE;
            	}
            	else
            	{
            		fmt = plyDataType.NONE;
            		continue;
            	}
            	
            	// data field
            	if(tokens[2].equalsIgnoreCase("x"))	// the blank is used to identify fields name
            	{
            		plyHeader.fields[plyFieldType.COORD.value()] = fmt;
            		plyHeader.fieldIds[plyFieldType.COORD.value()] = plyHeader.nFields++;
            	}
            	else if(tokens[2].equalsIgnoreCase("confidence"))
            	{
            		plyHeader.fields[plyFieldType.CONF.value()] = fmt;
            		plyHeader.fieldIds[plyFieldType.CONF.value()] = plyHeader.nFields++;
            	}
            	else if(tokens[2].equalsIgnoreCase("red") || tokens[2].equalsIgnoreCase("r"))
            	{
            		plyHeader.fields[plyFieldType.COLOR.value()] = fmt;
            		plyHeader.fieldIds[plyFieldType.COLOR.value()] = plyHeader.nFields++;
            		plyHeader.nColorChannal = 3;
            	}
            	else if(tokens[2].equalsIgnoreCase("alpha"))
            	{
            		plyHeader.nColorChannal = 4;
            	}
            	else if(tokens[2].equalsIgnoreCase("texture_u") || tokens[2].equalsIgnoreCase("u"))
            	{
            		plyHeader.fields[plyFieldType.UV.value()] = fmt;
            		plyHeader.fieldIds[plyFieldType.UV.value()] = plyHeader.nFields++;
            	}
            	else if(tokens[2].equalsIgnoreCase("normal_x") || tokens[2].equalsIgnoreCase("nx"))
            	{
            		plyHeader.fields[plyFieldType.NORM.value()] = fmt;
            		plyHeader.fieldIds[plyFieldType.NORM.value()] = plyHeader.nFields++;
            	}
            	else if(tokens[2].equalsIgnoreCase("intensity"))
            	{
            		plyHeader.fields[plyFieldType.INTENSITY.value()] = fmt;
            		plyHeader.fieldIds[plyFieldType.INTENSITY.value()] = plyHeader.nFields++;
            	}
            	else
            	{
            		if(line.contains("element"))
            		{
            			break;
            		}
            	}
            } while(line != null);// && line.contains(pattern));
            
            pattern = "element face";
            if(line.contains(pattern))
            {
            	plyHeader.nFaces = Integer.parseInt(line.substring(pattern.length()+1));
            }
            pattern = "property list";
            
            do
            {
            	line = reader.readLine();
            	String[] tokens = line.split(" ");
            	if(tokens.length < 4)
            		break;
            	if(tokens[2].equalsIgnoreCase("uchar") || tokens[2].equalsIgnoreCase("uint8"))
            	{
            		fmt = plyDataType.UCHAR;
            	}
            	else if(tokens[2].equalsIgnoreCase("uint16") || tokens[2].equalsIgnoreCase("ushort"))
            	{
            		fmt = plyDataType.USHORT;
            	}
            	else if(tokens[2].equalsIgnoreCase("int") || tokens[2].equalsIgnoreCase("int32"))
            	{
            		fmt = plyDataType.INT;
            	}
            	else if(tokens[2].equalsIgnoreCase("float"))
            	{
            		fmt = plyDataType.FLOAT;
            	}
            	else if(tokens[2].equalsIgnoreCase("float64") || tokens[2].equalsIgnoreCase("double"))
            	{
            		fmt = plyDataType.DOUBLE;
            	}
            	else
            	{
            		fmt = plyDataType.NONE;
            		continue;
            	}
            	
            	plyHeader.faceVertNumType = fmt;
            	
            	if(tokens[3].equalsIgnoreCase("uchar") || tokens[3].equalsIgnoreCase("uint8"))
            	{
            		fmt = plyDataType.UCHAR;
            	}
            	else if(tokens[3].equalsIgnoreCase("uint16") || tokens[3].equalsIgnoreCase("ushort"))
            	{
            		fmt = plyDataType.USHORT;
            	}
            	else if(tokens[3].equalsIgnoreCase("int") || tokens[3].equalsIgnoreCase("int32"))
            	{
            		fmt = plyDataType.INT;
            	}
            	else if(tokens[3].equalsIgnoreCase("float"))
            	{
            		fmt = plyDataType.FLOAT;
            	}
            	else if(tokens[3].equalsIgnoreCase("float64") || tokens[3].equalsIgnoreCase("double"))
            	{
            		fmt = plyDataType.DOUBLE;
            	}
            	else
            	{
            		fmt = plyDataType.NONE;
            		continue;
            	}
            	
            	plyHeader.faceIdxType = fmt;
            } while(line != null && line.contains(pattern));
            
            pattern = "end_header";
            while(line != null && !line.contains(pattern)) {
            	line = reader.readLine();
            }
            if(line == null)
            {
            	return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            
        }
		return plyHeader;
	}
	
	private boolean plyReadAscii(BufferedReader reader, PlyHeader plyHeader, Model model)
	{
		String line = null;
        try {
        	int[] fieldOffset = new int[FIELD_NUM];
    		for(int i=0; i<FIELD_NUM; i++)
    		{
    			fieldOffset[i] = -1;
    		}
    		int offset = 0;
    		fieldOffset[plyFieldType.COORD.value()] = offset;
    		offset += 3;
    		if(plyHeader.fieldIds[plyFieldType.COLOR.value()] >= 0)
			{
    			fieldOffset[plyFieldType.COLOR.value()] = offset;
    			offset += 4;
			}
    		if(plyHeader.fieldIds[plyFieldType.UV.value()] >= 0)
			{
    			fieldOffset[plyFieldType.UV.value()] = offset;
    			offset += 2;
			}
    		if(plyHeader.fieldIds[plyFieldType.NORM.value()] >= 0)
			{
    			fieldOffset[plyFieldType.NORM.value()] = offset;
    			offset += 3;
			}
    		float[] verts = new float[offset*plyHeader.nVerts];
        	for(int i=0; i<plyHeader.nVerts; i++)
        	{
        		line = reader.readLine();
        		String[] tokens = line.split(" ");
        		
        		for(int j=0; j<plyHeader.nFields; j++)
        		{
        			if(plyHeader.fieldIds[plyFieldType.COORD.value()] == j)
        			{
        				if(fieldOffset[plyFieldType.COORD.value()] < 0)
        					continue;
        				int startIdx = fieldOffset[plyFieldType.COORD.value()];
        				for(int k=0; k<3; k++)
        				{
        					verts[i*offset + startIdx + k] = Float.parseFloat(tokens[startIdx+k]);
        				}
        			}
        			else if(plyHeader.fieldIds[plyFieldType.COLOR.value()] == j)
        			{
        				if(fieldOffset[plyFieldType.COLOR.value()] < 0)
        					continue;
        				int startIdx = fieldOffset[plyFieldType.COLOR.value()];
        				for(int k=0; k<3; k++)
        				{
        					verts[i*offset + startIdx + k] = Float.parseFloat(tokens[startIdx+k]);
        				}
        				if(plyHeader.nColorChannal==4)
        				{
        					verts[i*offset + startIdx + 3] = Float.parseFloat(tokens[startIdx+3]);
        				}
        				else
        				{
        					verts[i*offset + startIdx + 3] = 255;
        				}
        			}
        			else if(plyHeader.fieldIds[plyFieldType.UV.value()] == j)
        			{
        				if(fieldOffset[plyFieldType.UV.value()] < 0)
        					continue;
        				int startIdx = fieldOffset[plyFieldType.UV.value()];
        				verts[i*offset + startIdx] = Float.parseFloat(tokens[startIdx]);
        				verts[i*offset + startIdx + 1] = 1.0f - Float.parseFloat(tokens[startIdx+1]);
        			}
        			else if(plyHeader.fieldIds[plyFieldType.NORM.value()] == j)
        			{
        				if(fieldOffset[plyFieldType.NORM.value()] < 0)
        					continue;
        				int startIdx = fieldOffset[plyFieldType.NORM.value()];
        				for(int k=0; k<3; k++)
        				{
        					verts[i*offset + startIdx + k] = Float.parseFloat(tokens[startIdx+k]);
        				}
        			}
        		}
        	}
        	//short[] indices = new short[plyHeader.nFaces * 3];
        	float[] vertices = new float[plyHeader.nFaces*3*offset];
        	int vi = 0;
        	for(int i=0; i<plyHeader.nFaces; i++)
        	{
        		line = reader.readLine();
        		String[] tokens = line.split(" ");
        		for(int k=0; k<3; k++)
        		{
        			int id = Integer.parseInt(tokens[k+1]);
        			for(int j=0; j<offset; j++)
            		{
        				vertices[vi++] = verts[id*offset + j];
        			}
        		}
        		//indices[i*3 + 0] = Short.parseShort(tokens[1]);
        		//indices[i*3 + 1] = Short.parseShort(tokens[2]);
        		//indices[i*3 + 2] = Short.parseShort(tokens[3]);
        	}
        	
        	model.vertices.setVertices(vertices, 0, vertices.length);
        	//model.vertices.setIndices(indices, 0, indices.length);
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            
        }
		return true;
	}
}
