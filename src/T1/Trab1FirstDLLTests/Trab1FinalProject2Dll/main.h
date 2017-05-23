#pragma once
#ifndef MAIN_H_
#define MAIN_H_


float energyProduction(int device_id);

int turnOn(int _state, int device_id);

int isOn(int device_id);

char * error();

float mean(int device_id);

float max(int device_id);

float min(int device_id);

int latitude();

int longitude();

#endif /* MAIN_H_ */