# traffic-signal-control-app-sumo-output-dashboard

A dashboard to measure SUMO's output mean travel time of all vehicles in the CST Machine Consciousness Traffic Signal Control App

## Usage

You should run the main SumoOutputDashboard program passing the path of the sumo output file as a parameter. The program will do all the rest, including automatically calculating the axis scale.

For instance, if the path is '/home/sumo/output.xml':
 
    $ ./SumoOutputDashboard /home/sumo/output.xml

![Dashboard](/dashboard.png)

## License

    Copyright 2016 CST-Group

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
