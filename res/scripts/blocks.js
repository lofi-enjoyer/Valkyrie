var onBreak = function(world, camera) {
    var position = world.rayCast(camera.getPosition(), camera.getDirection(), 10, false);
    if (position != undefined) {
        world.setBlock(0, position);
    }
}

var onPlace = function(world, camera, id) {
    var position = world.rayCast(camera.getPosition(), camera.getDirection(), 10, true);
    if (position != undefined) {
        world.setBlock(id, position);
    }
}

print('Blocks script loaded!');