Pawn = function (scene) {


 
    var Bomberman = PAWN.clone(PAWN.name);
    Bomberman.isVisible = true;

    var BomberMaterial = new BABYLON.StandardMaterial("mat", scene);
    BomberMaterial.diffuseColor = getPlayerColor(++PAWN.cloneCount);
    
    var multimat = new BABYLON.MultiMaterial("multi", scene);
    multimat.subMaterials.push(PAWN.material);
    multimat.subMaterials.push(BomberMaterial);
    Bomberman.material = multimat;

    Bomberman.subMeshes = [];
    var BomberVerticies = PAWN.getTotalVertices();
    console.log("TOT VERTS: " + BomberVerticies);
    
//MESH HAS 207936 indecies DONOT ASK HOW I KNOW!!!!!!!!!
        Bomberman.subMeshes.push(new BABYLON.SubMesh(0,0,BomberVerticies, 0, 51984, Bomberman));


        Bomberman.subMeshes.push(new BABYLON.SubMesh(1,0,BomberVerticies, 51984, 51984, Bomberman));

        Bomberman.subMeshes.push(new BABYLON.SubMesh(0,0,BomberVerticies, 103968, 103968, Bomberman));

        return Bomberman;
}

function getPlayerColor(playerNo) {

  playerNo %= 10;

  switch(playerNo) {
  
    case 1:
      return new BABYLON.Color3.FromHexString("#dd1c77");

    case 2:
      return new BABYLON.Color3.FromHexString("#31a354");

    case 3:
      return new BABYLON.Color3.FromHexString("#7fcdbb");

    case 4:
      return new BABYLON.Color3.FromHexString("#2c7fb8");

    case 5:
      return new BABYLON.Color3.FromHexString("#feb24c");

    case 6:
      return new BABYLON.Color3.FromHexString("#f03b20");

    case 7:
      return new BABYLON.Color3.FromHexString("#8856a7");

    case 8:
      return new BABYLON.Color3.FromHexString("#addd8e");

    case 9:
      return new BABYLON.Color3.FromHexString("#f7fcb9");

    case 10:
      return new BABYLON.Color3.FromHexString("#e5f5f9");

  }
}
