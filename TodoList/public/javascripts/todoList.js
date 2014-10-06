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
}