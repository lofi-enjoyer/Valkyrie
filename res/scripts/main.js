var File = Java.type("java.io.File")
var listHomeDir = new File(directory).listFiles();

for each (var file in listHomeDir) {
    if (!file.getName().equals("main.js"))
        load(file);
}