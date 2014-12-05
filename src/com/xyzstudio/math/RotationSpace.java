package com.xyzstudio.math;
import com.xyzstudio.common.CommonDefine;

public class RotationSpace {
	Matrix3x3 rotMat;
	Vector3 eularAngle;
	Vector3 so3;
	
	public RotationSpace()
	{
		rotMat = Matrix3x3.eye();
		eularAngle = Vector3.zeros();
		so3 = Vector3.zeros();
	}
	
	public static Vector3 Matrix2Eular(Matrix3x3 rMat)
	{
		Vector3 eular = new Vector3();
		/*
		float cy, tx, ty, sy;
		sy = -rMat.get(0, 2);
		cy = (float) -Math.sqrt(1 - sy*sy);
		eular.y((float) Math.atan2(sy, cy));
		if(Math.abs(cy) > CommonDefine.RATIO_EPSILON)	// no gimbal lock
		{
			tx = rMat.get(2, 2) / cy;
			ty = rMat.get(1, 2) / cy;
			eular.x((float) Math.atan2(ty, tx));

			tx = rMat.get(0, 0) / cy;
			ty = rMat.get(0, 1) / cy;
			eular.z((float) Math.atan2(ty, tx));
		}
		else						// gimbal lock
		{
			eular.x(0.0f);

			tx = rMat.get(2, 1) / cy;
			ty = rMat.get(1, 1) / cy;
			eular.z((float) Math.atan2(ty, tx));
		} */
		
		//this one from euclideanspace.com
		if(rMat.get(1, 0) > 1 - CommonDefine.RATIO_EPSILON)	//singularity at north pole
		{
			eular.heading((float) Math.atan2(rMat.get(0, 2), rMat.get(2, 2)));
			eular.attitude((float) Math.PI/2);
			eular.bank(0);
		}
		else if(rMat.get(1, 0) < -1 + CommonDefine.RATIO_EPSILON)	//singularity at south pole
		{
			eular.heading((float) Math.atan2(rMat.get(0, 2), rMat.get(2, 2)));
			eular.attitude((float) -Math.PI/2);
			eular.bank(0);
		}
		else
		{
			eular.heading((float) Math.atan2(-rMat.get(2, 0), rMat.get(0, 0)));
			eular.attitude((float) Math.atan2(-rMat.get(1, 2), rMat.get(1, 1)));
			eular.bank((float) Math.asin(rMat.get(1, 0)));
		}
		return eular;
	}
	
	public static Vector3 Matrix2Rodrigues(Matrix3x3 rMat)
	{
		Vector3 rodrigues = new Vector3();
		float theta = (float) Math.acos((rMat.trace()-1)*0.5);
		Vector3 v1 = new Vector3(rMat.get(2, 1), rMat.get(0, 2), rMat.get(1, 0));
		Vector3 v2 = new Vector3(rMat.get(1, 2), rMat.get(2, 0), rMat.get(0, 1));
		rodrigues = v1.minus(v2).divide( 2*((float) Math.sin(theta)) );
		rodrigues.mulToSelf(theta);
		return rodrigues;
	}
	
	public static Vector4 Matrix2AxisAngle(Matrix3x3 rMat)
	{
		Vector4 axisAngle;
		float theta = (float) Math.acos((rMat.trace()-1)*0.5);
		Vector3 v1 = new Vector3(rMat.get(2, 1), rMat.get(0, 2), rMat.get(1, 0));
		Vector3 v2 = new Vector3(rMat.get(1, 2), rMat.get(2, 0), rMat.get(0, 1));
		// (v1 - v2) / (2sin(theta))
		axisAngle = new Vector4(v1.minus(v2).divide( 2*((float) Math.sin(theta)) ), theta);
		return axisAngle;
	}
	
	public static Quaternion Matrix2Quaternion(Matrix3x3 rMat)
	{
		Quaternion q = new Quaternion();
		float trace = rMat.trace();
		if(trace > 0)
		{
			q.w((float) (Math.sqrt(1.0 + trace) / 2));
			float w4 = 1/(q.w()*4);
			q.x(rMat.get(2, 1) - rMat.get(1, 2) * w4);
			q.y(rMat.get(0, 2) - rMat.get(2, 0) * w4);
			q.z(rMat.get(1, 0) - rMat.get(0, 1) * w4);
		}
		else if(rMat.get(0, 0) > rMat.get(1, 1) && rMat.get(0, 0) > rMat.get(2, 2))
		{
			float w4 = (float) (Math.sqrt(1.0 + rMat.get(0, 0) - rMat.get(1, 1) - rMat.get(2, 2)) * 2);
			q.w(rMat.get(2, 1) - rMat.get(1, 2) / w4);
			q.x(0.25f*w4);
			q.y(rMat.get(1, 0) + rMat.get(0, 1) / w4);
			q.z(rMat.get(0, 2) + rMat.get(2, 0) / w4);
		}
		else if(rMat.get(1, 1) > rMat.get(2, 2))
		{
			float w4 = (float) (Math.sqrt(1.0 - rMat.get(0, 0) + rMat.get(1, 1) - rMat.get(2, 2)) * 2);
			q.w(rMat.get(0, 2) - rMat.get(2, 0) / w4);
			q.x(rMat.get(1, 0) + rMat.get(0, 1) / w4);
			q.y(0.25f*w4);
			q.z(rMat.get(2, 1) + rMat.get(1, 2) / w4);
		}
		else
		{
			float w4 = (float) (Math.sqrt(1.0 - rMat.get(0, 0) - rMat.get(1, 1) + rMat.get(2, 2)) * 2);
			q.w(rMat.get(1, 0) - rMat.get(0, 1) / w4);
			q.x(rMat.get(0, 2) + rMat.get(2, 0) / w4);
			q.y(rMat.get(2, 1) + rMat.get(1, 2) / w4);
			q.z(0.25f*w4);		
		}
		return q;
	}
	
	
	public static Vector3 Rodrigues2Eular(Vector3 vec)
	{
		Vector3 ret = new Vector3();
		float theta = vec.magnitude();
		vec.normalize();
		double s=Math.sin(theta);
		double c=Math.cos(theta);
		double hs=Math.sin(theta*0.5);
		double hc=Math.cos(theta*0.5);
		double t=1-c;
		if(vec.x()*vec.y()*t + vec.z()*s > 1 - CommonDefine.RATIO_EPSILON)
		{
			ret.heading((float) (2*Math.atan2(vec.x()*hs, hc)));
			ret.attitude((float) Math.PI/2);
			ret.bank(0);
		}
		else if(vec.x()*vec.y()*t + vec.z()*s < -1 + CommonDefine.RATIO_EPSILON)
		{
			ret.heading((float) (-2*Math.atan2(vec.x()*hs, hc)));
			ret.attitude((float) -Math.PI/2);
			ret.bank(0);
		}
		else
		{
			ret.heading((float) (Math.atan2(vec.y()*s - vec.x()*vec.z(), 1 - (vec.y()*vec.y()+vec.z()*vec.z())*t)));
			ret.attitude((float) Math.asin(vec.x()*vec.y()*t + vec.z()*s));
			ret.bank((float) (Math.atan2(vec.x()*s - vec.y()*vec.z(), 1 - (vec.x()*vec.x()+vec.z()*vec.z())*t)));
		}
		return ret;
	}
	
	public static Matrix3x3 Rodrigues2Matrix(Vector3 vec)
	{
		Matrix3x3 rMat = Matrix3x3.eye();
		float theta = vec.magnitude();
		vec.normalize();
		/* old method
		 * Matrix3x3 skewSymm = Matrix3x3.skewSymm(vec);
		 * rMat.addToSelf( skewSymm.mul((float) Math.sin(theta)) );
		 * rMat.addToSelf( skewSymm.mul(skewSymm).mul(1-(float) Math.cos(theta)) );*/
		float c = (float) Math.cos(theta);
		float s = (float) Math.sin(theta);
		float t = 1.0f - c;
		
		rMat.set(0, 0, c + vec.x()*vec.x()*t);
		rMat.set(1, 1, c + vec.y()*vec.y()*t);
		rMat.set(2, 2, c + vec.z()*vec.z()*t);
		
		float tmp1 = vec.x()*vec.y()*t;
		float tmp2 = vec.z()*s;
		rMat.set(1, 0, tmp1 + tmp2);
		rMat.set(0, 1, tmp1 - tmp2);
		
		tmp1 = vec.x()*vec.z()*t;
		tmp2 = vec.y()*s;
		rMat.set(2, 0, tmp1 - tmp2);
		rMat.set(0, 2, tmp1 + tmp2);
		
		tmp1 = vec.y()*vec.z()*t;
		tmp2 = vec.x()*s;
		rMat.set(2, 1, tmp1 + tmp2);
		rMat.set(1, 2, tmp1 - tmp2);
		
		return rMat;
	}
	
	public static Quaternion Rodrigues2Quaternion(Vector3 vec)
	{
		Quaternion q = new Quaternion();
		float theta = vec.magnitude();
		vec.normalize();
		float sinVal = (float) Math.sin(theta*0.5);
		q.w((float) Math.cos(theta*0.5));
		q.x(sinVal * vec.x());
		q.y(sinVal * vec.y());
		q.z(sinVal * vec.z());
		return q;
	}
	
	public static Vector4 Rodrigues2AxisAngle(Vector3 vec)
	{
		return new Vector4(vec);
	}
	
	public static Vector3 AxisAngle2Eular(Vector4 vec)
	{
		Vector3 ret = new Vector3();
		float theta = vec.w();
		double s=Math.sin(theta);
		double c=Math.cos(theta);
		double hs=Math.sin(theta*0.5);
		double hc=Math.cos(theta*0.5);
		double t=1-c;
		if(vec.x()*vec.y()*t + vec.z()*s > 1 - CommonDefine.RATIO_EPSILON)
		{
			ret.heading((float) (2*Math.atan2(vec.x()*hs, hc)));
			ret.attitude((float) Math.PI/2);
			ret.bank(0);
		}
		else if(vec.x()*vec.y()*t + vec.z()*s < -1 + CommonDefine.RATIO_EPSILON)
		{
			ret.heading((float) (-2*Math.atan2(vec.x()*hs, hc)));
			ret.attitude((float) -Math.PI/2);
			ret.bank(0);
		}
		else
		{
			ret.heading((float) (Math.atan2(vec.y()*s - vec.x()*vec.z(), 1 - (vec.y()*vec.y()+vec.z()*vec.z())*t)));
			ret.attitude((float) Math.asin(vec.x()*vec.y()*t + vec.z()*s));
			ret.bank((float) (Math.atan2(vec.x()*s - vec.y()*vec.z(), 1 - (vec.x()*vec.x()+vec.z()*vec.z())*t)));
		}
		return ret;
	}
	
	public static Matrix3x3 AxisAngle2Matrix(Vector4 vec)
	{
		Matrix3x3 rMat = Matrix3x3.eye();
		float theta = vec.w();
		/* old method
		 * Matrix3x3 skewSymm = Matrix3x3.skewSymm(vec);
		 * rMat.addToSelf( skewSymm.mul((float) Math.sin(theta)) );
		 * rMat.addToSelf( skewSymm.mul(skewSymm).mul(1-(float) Math.cos(theta)) );*/
		float c = (float) Math.cos(theta);
		float s = (float) Math.sin(theta);
		float t = 1.0f - c;
		
		rMat.set(0, 0, c + vec.x()*vec.x()*t);
		rMat.set(1, 1, c + vec.y()*vec.y()*t);
		rMat.set(2, 2, c + vec.z()*vec.z()*t);
		
		float tmp1 = vec.x()*vec.y()*t;
		float tmp2 = vec.z()*s;
		rMat.set(1, 0, tmp1 + tmp2);
		rMat.set(0, 1, tmp1 - tmp2);
		
		tmp1 = vec.x()*vec.z()*t;
		tmp2 = vec.y()*s;
		rMat.set(2, 0, tmp1 - tmp2);
		rMat.set(0, 2, tmp1 + tmp2);
		
		tmp1 = vec.y()*vec.z()*t;
		tmp2 = vec.x()*s;
		rMat.set(2, 1, tmp1 + tmp2);
		rMat.set(1, 2, tmp1 - tmp2);
		
		return rMat;
	}
	
	public static Quaternion AxisAngle2Quaternion(Vector4 vec)
	{
		Quaternion q = new Quaternion();
		float theta = vec.w();
		float sinVal = (float) Math.sin(theta*0.5);
		q.w((float) Math.cos(theta*0.5));
		q.x(sinVal * vec.x());
		q.y(sinVal * vec.y());
		q.z(sinVal * vec.z());
		return q;
	}
	
	public static Vector3 AxisAngle2Rodrigues(Vector4 vec)
	{
		return new Vector3(vec.x()*vec.w(), vec.y()*vec.w(), vec.z()*vec.w());
	}
	
	public static Vector3 Quaternion2Eular(Quaternion q)
	{
		Vector3 eular = new Vector3();
		float test = q.x()*q.y() + q.z()*q.w();
		if(test > 0.5 - CommonDefine.RATIO_EPSILON)
		{
			eular.heading((float) (2*Math.atan2(q.x(),  q.w())));
			eular.attitude((float) (Math.PI/2));
			eular.bank(0);
		}
		else if(test < -0.5 + CommonDefine.RATIO_EPSILON)
		{
			eular.heading((float) (-2*Math.atan2(q.x(),  q.w())));
			eular.attitude((float) (-Math.PI/2));
			eular.bank(0);
		}
		else
		{
			float sqX = q.x()*q.x();
			float sqY = q.y()*q.y();
			float sqZ = q.z()*q.z();
			eular.heading((float) Math.atan2(2*q.y()*q.w() - 2*q.x()*q.z(), 1 - 2*sqY - 2*sqZ));
			eular.attitude((float) Math.asin(2*test));
			eular.bank((float) Math.atan2(2*q.x()*q.w() - q.y()*q.z(), 1 - 2*sqX - 2*sqZ));
		}
		return eular;
	}
	
	public static Vector3 Quaternion2Rodrigues(Quaternion q)
	{
		Vector3 rod = Vector3.zeros();
		float theta = (float) (2*Math.acos(q.w()));
		float sinVal = (float) (Math.sqrt(1 - q.w()*q.w()));
		if(sinVal > CommonDefine.RATIO_EPSILON)
		{			
			rod.x( q.x()/sinVal );
			rod.y( q.y()/sinVal );
			rod.z( q.z()/sinVal );
		}
		else
		{
			//rod.x(q.x());
			//rod.y(q.y());
			//rod.z(q.z());
			rod.x(1);
			rod.y(0);
			rod.z(0);
		}
		rod.mulToSelf(theta);
		return rod;
	}
	
	public static Vector4 Quaternion2AxisAngle(Quaternion q)
	{
		Vector3 rod = Vector3.zeros();
		float theta = (float) (2*Math.acos(q.w()));
		float sinVal = (float) (Math.sqrt(1 - q.w()*q.w()));
		if(sinVal > CommonDefine.RATIO_EPSILON)
		{			
			rod.x( q.x()/sinVal );
			rod.y( q.y()/sinVal );
			rod.z( q.z()/sinVal );
		}
		else
		{
			//rod.x(q.x());
			//rod.y(q.y());
			//rod.z(q.z());
			rod.x(1);
			rod.y(0);
			rod.z(0);
		}
		return new Vector4(rod, theta);
	}
	
	public static Matrix3x3 Quaternion2Matrix(Quaternion q)
	{
		Matrix3x3 rMat = Matrix3x3.eye();
		rMat.set(0, 0, 1 - 2*q.y()*q.y() - 2*q.z()*q.z());
		rMat.set(0, 1, 2*q.x()*q.y() - 2*q.z()*q.w());
		rMat.set(0, 2, 2*q.x()*q.z() + 2*q.y()*q.w());
		
		rMat.set(1, 0, 2*q.x()*q.y() + 2*q.z()*q.w());
		rMat.set(1, 1, 1 - 2*q.x()*q.x() - 2*q.z()*q.z());
		rMat.set(1, 2, 2*q.y()*q.z() - 2*q.x()*q.w());
		
		rMat.set(2, 0, 2*q.x()*q.z() - 2*q.y()*q.w());
		rMat.set(2, 1, 2*q.y()*q.z() + 2*q.x()*q.w());
		rMat.set(2, 2, 1 - 2*q.x()*q.x() - 2*q.y()*q.y());
		return rMat;
	}
	
	public static Vector3 Eular2Rodrigues(Vector3 vec)
	{
		Vector3 rod = new Vector3();
		double c1 = Math.cos(vec.heading()/2);
	    double s1 = Math.sin(vec.heading()/2);
	    double c2 = Math.cos(vec.attitude()/2);
	    double s2 = Math.sin(vec.attitude()/2);
	    double c3 = Math.cos(vec.bank()/2);
	    double s3 = Math.sin(vec.bank()/2);
	    double c1c2 = c1*c2;
	    double s1s2 = s1*s2;
	    double w =c1c2*c3 - s1s2*s3;
		rod.x((float) (c1c2*s3 + s1s2*c3));
		rod.y((float) (s1*c2*c3 + c1*s2*s3));
		rod.z((float) (c1*s2*c3 - s1*c2*s3));
		double angle = 2 * Math.acos(w);
		rod.normalize();
		rod.mulToSelf((float)angle);
		return rod;
	}
	
	public static Vector4 Eular2AxisAngle(Vector3 vec)
	{
		Vector3 rod = new Vector3();
		double c1 = Math.cos(vec.heading()/2);
	    double s1 = Math.sin(vec.heading()/2);
	    double c2 = Math.cos(vec.attitude()/2);
	    double s2 = Math.sin(vec.attitude()/2);
	    double c3 = Math.cos(vec.bank()/2);
	    double s3 = Math.sin(vec.bank()/2);
	    double c1c2 = c1*c2;
	    double s1s2 = s1*s2;
	    double w =c1c2*c3 - s1s2*s3;
		rod.x((float) (c1c2*s3 + s1s2*c3));
		rod.y((float) (s1*c2*c3 + c1*s2*s3));
		rod.z((float) (c1*s2*c3 - s1*c2*s3));
		double angle = 2 * Math.acos(w);
		rod.normalize();
		return new Vector4(rod, (float) angle);
	}
	
	public static Matrix3x3 Eular2Matrix(Vector3 eular)
	{
		Matrix3x3 rMat = Matrix3x3.eye();
		float sinA = (float)Math.sin(eular.attitude());	//pitch
        float cosA = (float)Math.cos(eular.attitude());
        float sinB = (float)Math.sin(eular.bank());	//roll
        float cosB = (float)Math.cos(eular.bank());
        float sinH = (float)Math.sin(eular.heading());	//yaw
        float cosH = (float)Math.cos(eular.heading());
        
        /*rMat.set(0, 0, cosH*cosA);
        rMat.set(0, 1, sinH*sinB - cosH*sinA*cosB);
        rMat.set(0, 2, cosH*sinA*sinB + sinH*cosB);
        
        rMat.set(1, 0, sinA);
        rMat.set(1, 1, cosA*cosB);
        rMat.set(1, 2, -cosA*sinB);
        
        rMat.set(2, 0, -sinH*cosA);
        rMat.set(2, 1, sinH*sinA*cosB + cosH*sinB);
        rMat.set(2, 2, -sinH*sinA*sinB + cosH*cosB);
        */
        float[] xM = new float[9];
        float[] yM = new float[9];
        float[] zM = new float[9];
        xM[0] = 1.0f; xM[1] = 0.0f; xM[2] = 0.0f;
        xM[3] = 0.0f; xM[4] = cosA; xM[5] = sinA;
        xM[6] = 0.0f; xM[7] = -sinA; xM[8] = cosA;
     
        // rotation about y-axis (roll)
        yM[0] = cosB; yM[1] = 0.0f; yM[2] = sinB;
        yM[3] = 0.0f; yM[4] = 1.0f; yM[5] = 0.0f;
        yM[6] = -sinB; yM[7] = 0.0f; yM[8] = cosB;
     
        // rotation about z-axis (azimuth)
        zM[0] = cosH; zM[1] = sinH; zM[2] = 0.0f;
        zM[3] = -sinH; zM[4] = cosH; zM[5] = 0.0f;
        zM[6] = 0.0f; zM[7] = 0.0f; zM[8] = 1.0f;
        Matrix3x3 xMat = new Matrix3x3(xM);
        Matrix3x3 yMat = new Matrix3x3(yM);
        Matrix3x3 zMat = new Matrix3x3(zM);
        
        rMat = xMat.mul(yMat);
        rMat = zMat.mul(rMat);
		return rMat;
	}
	
	public static Quaternion Eular2Quaternion(Vector3 vec)
	{
		Quaternion q = new Quaternion();
		double c1 = Math.cos(vec.heading()/2);
	    double s1 = Math.sin(vec.heading()/2);
	    double c2 = Math.cos(vec.attitude()/2);
	    double s2 = Math.sin(vec.attitude()/2);
	    double c3 = Math.cos(vec.bank()/2);
	    double s3 = Math.sin(vec.bank()/2);
	    double c1c2 = c1*c2;
	    double s1s2 = s1*s2;
	    q.w((float) (c1c2*c3 - s1s2*s3));
	  	q.x((float) (c1c2*s3 + s1s2*c3));
		q.y((float) (s1*c2*c3 + c1*s2*s3));
		q.z((float) (c1*s2*c3 - s1*c2*s3));
		return q;
	}
}
