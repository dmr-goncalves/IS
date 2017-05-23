#include "main.h"
#include "MainClass.h"
#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <sstream>

jint state[10];
jfloat value = 0;
float measures[10][20];
int i[10];
jfloat maxValue[10];
jfloat minValue[10];
jfloat meanValue[10];
jint Longitude = 0;
jint Latitude = 0;
char * Error = 0;
jint maxDefault = 0;
jint minDefault = 0;
jint meanDefault = 0;

JNIEXPORT jfloat JNICALL Java_MainClass_energyProduction (JNIEnv *, jclass, jint device_id) {
	/*value = rand() % 100;
	measures[device_id][i[device_id]] = value;
	i[device_id]++;*/
	return energyProduction(device_id);
}

JNIEXPORT jint JNICALL Java_MainClass_turnOn (JNIEnv *, jclass, jint _state, jint device_id) {
	/*state[device_id] = _state;*/
	return turnOn(_state, device_id);
}

JNIEXPORT jint JNICALL Java_MainClass_isOn(JNIEnv *, jclass, jint device_id) {
	return isOn(device_id);
}

JNIEXPORT jstring JNICALL Java_MainClass_error(JNIEnv * _env, jclass) {
	Error = error(); // rand() % 4;
	std::stringstream str;
	str << Error;
	return _env->NewStringUTF(str.str().c_str());
}

JNIEXPORT jfloat JNICALL Java_MainClass_max (JNIEnv *, jclass, jint device_id) {
	/*if (i[device_id] == 0) {
		return maxDefault;
	}
	else {
		for (int x = 0; x < i[device_id]; x++) {
			if (maxValue[device_id] < measures[device_id][x]) {
				maxValue[device_id] = measures[device_id][x];
			}
		}
		return maxValue[device_id];
	}*/
	return max(device_id);

}

JNIEXPORT jfloat JNICALL Java_MainClass_min (JNIEnv *, jclass, jint device_id) {
	/*if (i[device_id] == 0) {
		return minDefault;
	}
	else {
		for (int x = 0; x < i[device_id]; x++) {
			if (minValue[device_id] > measures[device_id][x]) {
				minValue[device_id] = measures[device_id][x];
			}
		}
		return minValue[device_id];
	}*/
	return min(device_id);
}

JNIEXPORT jfloat JNICALL Java_MainClass_mean(JNIEnv *, jclass, jint device_id) {
	/*if (i[device_id] == 0) {
		return meanDefault;
	}
	else {
		for (int x = 0; x < i[device_id]; x++) {
			meanValue[device_id] = meanValue[device_id] + measures[device_id][x];
		}
		meanValue[device_id] = meanValue[device_id] / i[device_id];
		return meanValue[device_id];
	}*/
	return mean(device_id);
}

JNIEXPORT jint JNICALL Java_MainClass_latitude (JNIEnv *, jclass) {
	return /*rand() % 51*/ latitude();
}

JNIEXPORT jint JNICALL Java_MainClass_longitude(JNIEnv *, jclass) {
	return /*rand() % 51*/ longitude();
}