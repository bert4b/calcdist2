var guiAppCbClient = angular.module('guiApp', ['ui.bootstrap']);
guiAppCbClient.controller('ConnectionController',function($scope){
   $scope.isCollapsed=true;
});
guiAppCbClient.factory('connections', function() {
    return connectionEstablishedIndicator;
});
guiAppCbClient.controller('JobController', function ($scope,$rootScope,connections,$log) {
    $scope.nodeSize=0;
    $scope.isNotAllowedToExecute=true;
    $scope.$log = $log;
    $scope.connections=connections;
    $rootScope.$watch('connections', function(newValue, oldValue) {
        if (newValue!=undefined){
            $scope.nodeSize = newValue;
       }
    });


    $scope.double = function (value) {
        return value * 2;
    };
    $scope.results=undefined;
    $scope.execute = function (tasks) {
        tasks.forEach(function (task) {

            $scope.executeTask(task,tasks);
        });
        $scope.results=tasks[0].result;
    };
    $scope.executeTask = function (task,parent) {

        if (task.vertical != undefined) {
            if (task.process) {
                //if ( task.type=='p'){
                //    console.info('parallel'+task.title);
                //}else{
                    var input={tasks:$scope.tasks,parent:parent,inputs:[10000,3.2,6,7]};

                var result=eval(task.content);
                if (task.propagate){
                    parent.result=result;
                }
                task.result=result;
                //}

            }

            task.vertical.forEach(function (vertical) {
                $scope.executeTask(vertical,task);
            });


        }
        if (task.parallel != undefined) {


            task.parallel.forEach(function (parallel) {
                $scope.executeTask(parallel);
            });


        }
    };
    $scope.tasks = [
        {
            title: 'Sequential 0',
            id:1,
            process: true,
            content: 'result=0',
            'vertical': [{
                title: 'Sequential 1',
                id:2,
                process: true,
                content: 'result=0',
                type: 's',
                'vertical': []
            },
                {
                    title: 'Container 2',
                    type: 'c',
                    id:3,
                    process: false,
                    'parallel': [{
                        title: 'Parallel 2a',
                        id:30,
                        process: true,
                        type: 'p',
                        content: 'result=input.inputs[0]*4',
                        'vertical': []
                    },
                        {
                            title: 'Parallel 2b',
                            id:31,
                            process: true,
                            type: 'p',
                            content: 'result=input.inputs[1]/12',

                            'vertical': []
                        }
                    ]
                },
                {
                    title: 'Sequential 3',
                    id:4,
                    process: true,
                    content: 'result=parent.vertical[1].parallel[0].result*parent.vertical[1].parallel[1].result',
                    propagate:true,
                    'vertical': []
                }
            ]
        }

    ];
});
