var scope = null
function TodoListController($scope, $http) {
    scope = $scope
    $scope.model = {};
    $scope.model.tasks = [];

    $http.get("/task").success(function (data) {
        $scope.model.tasks = data
    })

    $scope.interface = {};
    $scope.interface.order = "label"
    $scope.interface.reverse = false;
    $scope.interface.label = ""
    $scope.interface.done = ""
    $scope.interface.date = ""

    $scope.functions = {}
    $scope.functions.createTask = function () {
        alert("entrando")
        $http({
            url: "/task",
            method: "POST",
            data: '{"label":"' + $scope.interface.label + '","date": "' + $scope.interface.date + '","done":"' + $scope.interface.done + '"}',
            headers: {
                'Content-Type': 'application/json'
            }
        })
    }

}