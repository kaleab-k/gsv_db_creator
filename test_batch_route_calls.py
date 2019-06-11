import subprocess
import os

# location
# size (width, heigh)
route = 10
if route == 1:
    width_from = 40.601780
    heigh_from = -3.708161

    width_to = 40.602911
    heigh_to = -3.700484

elif route == 2:
    width_from = 40.416869
    heigh_from = -3.703500

    width_to = 40.420380
    heigh_to = -3.706163

elif route == 3:
    width_from = 40.434305
    heigh_from = -3.700126

    width_to = 40.432905
    heigh_to = -3.698147

elif route == 4:
    width_from = 40.426288
    heigh_from = -3.683772

    width_to = 40.427505
    heigh_to = -3.681353

elif route == 5:
    width_from = 40.408344
    heigh_from = -3.702783

    width_to = 40.407820
    heigh_to = -3.698536

elif route == 6:
    width_from = 40.345037
    heigh_from = -3.828949

    width_to = 40.346369
    heigh_to = -3.828398

elif route == 7:
    width_from = 55.678915
    heigh_from = 12.575720

    width_to = 55.678618
    heigh_to = 12.581068

elif route == 8:
    width_from = 41.388520
    heigh_from = 2.171659

    width_to = 41.385437
    heighto = 2.171648

elif route == 9:
    width_from = 40.140298
    heigh_from = -3.422104

    width_to = 40.140257
    heigh_to = -3.415723

elif route == 10:
    width_from = 23.806427
    heigh_from = 90.373895

    width_to = 23.801778
    heigh_to = 90.370984


# key
key = "<YOUR_API_YEK"

# heading (0:360)
heading_i = 0
heading_o = 360
n_heading = 3
points_heading = int(heading_o/n_heading)

# fov (0:120)
fov_i = 20
fov_o = 120
n_fov = 6
n_fov2 = 4
points_fov = int((fov_o - fov_i)/(n_fov-1))

# pitch (-90, 90)
pitch_i = -40
pitch_o = 40
n_pitch = 2
points_pitch = int((pitch_o - pitch_i)/(n_pitch-1))
pitch = [-30, -20, -10, 10, 20, 30]

# travel mode DRIVING 0; WALKING 1; TRANSIT 2; BICYCLING 3
modo = 0

# name dir
dir = "bd/route" + str(route) + "/"
name = dir + ".json"

# Create the dir if it doesnt exists
try:
  os.stat(dir)
except:
    os.mkdir(dir)

# DRIVING MODE
if modo == 0:
    # Normal
    for x in range(0, n_fov):
        linea_ejecucion_1 = 'java -jar StreetviewExtractor-1.0-SNAPSHOT.jar --from ' + str(width_from) + ',' + str(heigh_from) + ' --to ' + str(width_to) + ',' + str(heigh_to) + ' -i -o ' + name + ' -a ' + key + ' --head --fov ' + str(int(fov_i))
        print(linea_ejecucion_1)
        subprocess.run(linea_ejecucion_1)
        fov_i = fov_i + points_fov

    fov_i = 20

    # Changing pitch
    for p in range(0, 6):
        linea_ejecucion_1 = 'java -jar StreetviewExtractor-1.0-SNAPSHOT.jar --from ' + str(width_from) + ',' + str(heigh_from) + ' --to ' + str(width_to) + ',' + str(heigh_to) + ' -i -o ' + name + ' -a ' + key + ' --head --fov ' + str(int(fov_o)) + ' --pitch ' + str(int(pitch[p]))
        print(linea_ejecucion_1)
        subprocess.run(linea_ejecucion_1)

    fov_i = fov_o - ((n_fov2 - 1) * points_fov);

    # heading +90
    heading = 90
    for x in range(0, n_fov2):
        linea_ejecucion_1 = 'java -jar StreetviewExtractor-1.0-SNAPSHOT.jar --from ' + str(width_from) + ',' + str(heigh_from) + ' --to ' + str(width_to) + ',' + str(heigh_to) + ' -i -o ' + name + ' -a ' + key + ' --head --fov ' + str(int(fov_i)) + ' --heading ' + str(int(heading))
        print(linea_ejecucion_1)
        subprocess.run(linea_ejecucion_1)
        fov_i = fov_i + points_fov


    # Changing pitch
    for p in range(0, 6):
        linea_ejecucion_1 = 'java -jar StreetviewExtractor-1.0-SNAPSHOT.jar --from ' + str(width_from) + ',' + str(heigh_from) + ' --to ' + str(width_to) + ',' + str(heigh_to) + ' -i -o ' + name + ' -a ' + key + ' --head --fov ' + str(int(fov_o)) + ' --pitch ' + str(int(pitch[p])) + ' --heading ' + str(int(heading))
        print(linea_ejecucion_1)
        subprocess.run(linea_ejecucion_1)


    fov_i = 20

# WALKING MODE
modo = 1

if modo == 1:
    # Normal
    for x in range(0, n_fov):
        linea_ejecucion_1 = 'java -jar StreetviewExtractor-1.0-SNAPSHOT.jar --from ' + str(width_from) + ',' + str(heigh_from) + ' --to ' + str(width_to) + ',' + str(heigh_to) + ' -i -o ' + name + ' -a ' + key + ' --head --fov ' + str(int(fov_i)) + ' --mode WALKING'
        print(linea_ejecucion_1)
        subprocess.run(linea_ejecucion_1)
        fov_i = fov_i + points_fov

    fov_i = 20

    # Changing pitch
    for p in range(0, 6):
        linea_ejecucion_1 = 'java -jar StreetviewExtractor-1.0-SNAPSHOT.jar --from ' + str(width_from) + ',' + str(heigh_from) + ' --to ' + str(width_to) + ',' + str(heigh_to) + ' -i -o ' + name + ' -a ' + key + ' --head --fov ' + str(int(fov_o)) + ' --pitch ' + str(int(pitch[p])) + ' --mode WALKING'
        print(linea_ejecucion_1)
        subprocess.run(linea_ejecucion_1)

    fov_i = fov_o - ((n_fov2 - 1) * points_fov)
    # +90
    heading = 90
    for x in range(0, n_fov2):
        linea_ejecucion_1 = 'java -jar StreetviewExtractor-1.0-SNAPSHOT.jar --from ' + str(width_from) + ',' + str(heigh_from) + ' --to ' + str(width_to) + ',' + str(heigh_to) + ' -i -o ' + name + ' -a ' + key + ' --head --fov ' + str(int(fov_i)) + ' --heading ' + str(int(heading)) + ' --mode WALKING'
        print(linea_ejecucion_1)
        subprocess.run(linea_ejecucion_1)
        fov_i = fov_i + points_fov

    # Changing pitch
    for p in range(0, 6):
        linea_ejecucion_1 = 'java -jar StreetviewExtractor-1.0-SNAPSHOT.jar --from ' + str(width_from) + ',' + str(heigh_from) + ' --to ' + str(width_to) + ',' + str(heigh_to) + ' -i -o ' + name + ' -a ' + key + ' --head --fov ' + str(int(fov_o)) + ' --pitch ' + str(int(pitch[p])) + ' --heading ' + str(int(heading)) + ' --mode WALKING'
        print(linea_ejecucion_1)
        subprocess.run(linea_ejecucion_1)


