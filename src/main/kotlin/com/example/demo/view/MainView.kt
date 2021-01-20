package com.example.demo.view

import javafx.scene.control.Alert
import javafx.scene.control.TextField
import javafx.scene.image.Image
import org.apache.commons.io.FileUtils
import tornadofx.*
import java.io.File
import java.net.URL
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors
import javax.swing.ImageIcon
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
    private val logo: Image = Image("file:///Users/carlossoler/IdeaProjects/CopyAssets/src/main/resources/odiloLogo.png")
    var iconURL: URL = URL("file:///Users/carlossoler/IdeaProjects/CopyAssets/src/main/resources/odiloLogo.png")
    var imageIcon: java.awt.Image = ImageIcon(iconURL).image
    private val dockLogo: java.awt.Image = imageIcon

    init {
        com.apple.eawt.Application.getApplication().setDockIconImage(dockLogo)
        addStageIcon(logo)
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
                        if (brandingRoot != null && assetsToCopy != null) {
                            brandings =
                                Arrays.stream(brandingRoot?.listFiles()).filter { it.name != ".DS_Store" }.toList();
                            assets = Arrays.stream(assetsToCopy?.listFiles()).collect(Collectors.toList());
                            brandings?.forEach { branding ->
                                if (branding.path != assetsToCopy?.parent) {
                                    val brandingAssets =
                                        branding.listFiles()?.filter { it.name == "Assets.xcassets" }?.get(0)
                                            ?.listFiles()
                                            ?.toList()
                                    assets?.forEach { assetToCompare ->
                                        if (!brandingAssets?.map { it.name }?.contains(assetToCompare.name)!!) {
                                            copyAssets.add(assetToCompare)
                                        }
                                    }
                                    if (!copyAssets.isEmpty()) {
                                        copyAssets.forEach { asset ->
                                            FileUtils.copyDirectory(
                                                asset,
                                                Paths.get(
                                                    branding.listFiles()?.filter { it.name == "Assets.xcassets" }
                                                        ?.get(0)
                                                        ?.toString() + "/" + asset.name
                                                ).toFile()
                                            )
                                            println(
                                                "Copying " + asset.name + " to " + branding.listFiles()
                                                    ?.filter { it.name == "Assets.xcassets" }?.get(0)!!
                                            )
                                        }
                                    } else {
                                        alert(
                                            Alert.AlertType.ERROR,
                                            "Error",
                                            "No hay ningun archivo para copiar",
                                            owner = currentWindow
                                        )
                                    }
                                }
                            }
                        } else {
                            alert(
                                Alert.AlertType.ERROR,
                                "Error",
                                "Tienes que seleccionar la carpeta root de Targets y la carpeta principal de Assets",
                                owner = currentWindow
                            )
                        }
                    }
                }
            }
        }
    }
}