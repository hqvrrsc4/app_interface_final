<?php
    $con = mysqli_connect("localhost", "id2004494_hxlee0309_username", "hamish0309", "id2004494_hxlee0309_database");

    if (mysqli_connect_errno()) {
        printf("Connect failed: %s\n", mysqli_connect_error());
        exit();
    }

    // printf("Connect established\n");

    // mysqli_autocommit($con, FALSE);
    // mysqli_query($con, "INSERT INTO user(name, username, age, password) VALUES ('w', 'w', 3, 'e')");
    // if (!mysqli_commit($con)) {
    //     print("Transaction failed.\n");
    //     exit();
    // }
    // mysqli_close($con);

     $name = $_POST["name"];
     $username = $_POST["username"];
     $age = $_POST["age"];
     $password = $_POST["password"];

  //  $name = $_GET["name"];
  //  $age = $_GET["age"];
  //  $username = $_GET["username"];
  //  $password = $_GET["password"];

  //  printf("Name: %s\nUsername: %s\nAge: %d\nPassword: %s\n", $name, $username, $age, $password);

    $statement = mysqli_prepare($con, "INSERT INTO user(name, username, age, password) VALUES (?, ?, ?, ?)");
    mysqli_stmt_bind_param($statement, "ssis", $name, $username, $age, $password);
    mysqli_stmt_execute($statement);
    mysqli_commit();
    
    $response = array();
    $response["success"] = true;  
    
    echo json_encode($response);
?>