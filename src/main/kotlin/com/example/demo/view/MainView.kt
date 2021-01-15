package com.example.demo.view

import javafx.scene.control.Button
import javafx.scene.control.TextField
import tornadofx.*
import java.io.File

class MainView : View("DirectoryChooser") {
    override val root = borderpane()

    private lateinit var brandingRootPath: TextField
    private lateinit var assetsToCopyPath: TextField
    private var assetsToCopyRoot: File? = null
    private var brandingRoot: File? = null

    init {
        with(root) {
            title = "CopyAssets"
            center = form {
                fieldset("Selecciona carpetas") {
                    field("Carpeta raiz de branding") {
                        hbox {
                            brandingRootPath = textfield()
                            button("open") {
                                action {
                                    brandingRoot = chooseDirectory("Single + non/block")
                                    if(brandingRoot != null) {
                                        brandingRootPath.text = "${brandingRoot}"
                                    }
                                }
                            }
                        }
                    }
                    field("Carpeta principal assets") {
                        hbox {
                            assetsToCopyPath = textfield()
                            button("open") {
                                action {
                                    assetsToCopyRoot = chooseDirectory("Single + non/block")
                                    if(assetsToCopyRoot != null) {
                                        assetsToCopyPath.text = "${assetsToCopyRoot}"
                                    }
                                }
                            }
                        }
                    }
                    button("Copiar").setOnAction {
                        println("Button 1 Pressed")
                        title = "Ola k tal"
                    }
                }
            }
        }
    }
}