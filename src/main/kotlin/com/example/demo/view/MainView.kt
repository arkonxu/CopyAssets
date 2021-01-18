package com.example.demo.view

import javafx.scene.control.Alert
import javafx.scene.control.TextField
import org.apache.commons.io.FileUtils
import tornadofx.*
import java.io.File
import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.stream.Collectors
import kotlin.streams.toList

class MainView : View("DirectoryChooser") {
    override val root = borderpane()

    private lateinit var brandingRootTf: TextField
    private lateinit var assetsToCopyTf: TextField
    private var assetsToCopy: File? = null
    private var brandingRoot: File? = null
    private var brandings: List<File>? = null
    private var assets: List<File>? = null
    private var copyAssets: MutableList<File> = mutableListOf()

    init {
        with(root) {
            title = "CopyAssets"
            center = form {
                fieldset("Selecciona carpetas") {
                    field("Carpeta raiz de brandings") {
                        hbox {
                            brandingRootTf = textfield()
                            button("open") {
                                action {
                                    brandingRoot = chooseDirectory("Single + non/block")
                                    if (brandingRoot != null) {
                                        brandingRootTf.text = "${brandingRoot}"
                                    }
                                }
                            }
                        }
                    }
                    field("Carpeta principal assets") {
                        hbox {
                            assetsToCopyTf = textfield()
                            button("open") {
                                action {
                                    assetsToCopy = chooseDirectory("Single + non/block")
                                    if (assetsToCopy != null) {
                                        assetsToCopyTf.text = "${assetsToCopy}"
                                    }
                                }
                            }
                        }
                    }
                    button("Copiar").setOnAction {
                        brandings = Arrays.stream(brandingRoot?.listFiles()).filter { it.name != ".DS_Store" }.toList();
                        assets = Arrays.stream(assetsToCopy?.listFiles()).collect(Collectors.toList());
                        brandings?.forEach { branding ->
                            val brandingAssets =
                                branding.listFiles()?.filter { it.name == "Assets.xcassets" }?.get(0)?.listFiles()
                                    ?.toList()
                            assets?.forEach { assetToCompare ->
                                if(!brandingAssets?.map { it.name }?.contains(assetToCompare.name)!!) {
                                    copyAssets.add(assetToCompare)
                                }
                            }
                            if(!copyAssets.isEmpty()) {
                                copyAssets.forEach {
                                    Files.copy(
                                        it.toPath(),
                                        Paths.get(branding.listFiles()?.filter { it.name == "Assets.xcassets" }?.get(0)
                                            ?.toString() + "/" + it.name
                                        ),
                                        StandardCopyOption.REPLACE_EXISTING
                                    )
                                    FileUtils.copyDirectory(
                                        it,
                                        Paths.get(branding.listFiles()?.filter { it.name == "Assets.xcassets" }?.get(0)
                                            ?.toString() + "/" + it.name
                                        ).toFile()
                                    )
                                    println(
                                        "Copying " + it.name + " to " + branding.listFiles()
                                            ?.filter { it.name == "Assets.xcassets" }?.get(0)!!
                                    )
                                }
                            } else {
                                alert(Alert.AlertType.ERROR, "Error", "No hay ningun archivo para copiar", owner = currentWindow)
                            }
                        }
                    }
                }
            }
        }
    }
}