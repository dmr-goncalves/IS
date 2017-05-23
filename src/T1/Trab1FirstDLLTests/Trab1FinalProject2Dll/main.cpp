#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include "main.h"

void main() {
	srand(time(NULL));

	for (int i = 0; i < 10; i++) {
		printf("\nenergyProduction: %f", energyProduction(1));
	}
	printf("\nstate: %d", turnOn(1,1));
	printf("\nisOn: %d", isOn(1));
	printf("\nstate: %d", turnOn(1,2));
	printf("\nisOn: %d", isOn(2));
	printf("\nerror: %c", *error());
	printf("\nmax: %f", max(1));
	printf("\nmin: %f", min(1));
	printf("\nmean: %f", mean(1));
	printf("\nlatitude: %d", latitude());
	printf("\nlongitude: %d", longitude());
	getchar(); 
}