#include <stdio.h>
#include <stdlib.h>
#include <sstream>
#include <string>

int state[10];
float value = 0;
float measures[10][20];
int i[10];
float maxValue[10] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
float minValue[10] = {99, 99, 99, 99, 99, 99, 99, 99, 99, 99};
float meanValue[10] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
int Longitude = 0;
int Latitude = 0;
char Error;
int errorValue = 0;
int maxDefault = 0;
int minDefault = 0;
int meanDefault = 0;

__declspec(dllexport) float energyProduction(int device_id) {
	value = rand() % 100;
	measures[device_id][i[device_id]] = value;
	i[device_id]++;
	return value;
}

__declspec(dllexport) int turnOn(int _state, int device_id) {
	state[device_id] = _state;
	return state[device_id];
}

__declspec(dllexport) int isOn(int device_id) {
	return state[device_id];
}

__declspec(dllexport) char * error() {
	errorValue = rand() % 4;
	Error = '0' + errorValue;
	return &Error;
}

__declspec(dllexport) float max(int device_id) {
	if (i[device_id] == 0) {
		return maxDefault;
	}
	else {
		for (int x = 0; x < i[device_id]; x++) {
			if (maxValue[device_id] < measures[device_id][x]) {
				maxValue[device_id] = measures[device_id][x];
			}
		}
		return maxValue[device_id];
	}
}

__declspec(dllexport) float min(int device_id) {
	if (i[device_id] == 0) {
		return minDefault;
	}
	else {
		for (int x = 0; x < i[device_id]; x++) {
			if (minValue[device_id] > measures[device_id][x]) {
				minValue[device_id] = measures[device_id][x];
			}
		}
		return minValue[device_id];
	}
}

__declspec(dllexport) float mean(int device_id) {
	if (i[device_id] == 0) {
		return meanDefault;
	}
	else {
		for (int x = 0; x < i[device_id]; x++) {
			meanValue[device_id] = meanValue[device_id] + measures[device_id][x];
		}
		meanValue[device_id] = meanValue[device_id] / i[device_id];
		return meanValue[device_id];
	}
}

__declspec(dllexport) int latitude() {
	return rand() % 51;
}

_declspec(dllexport) int longitude() {
	return rand() % 51;
}