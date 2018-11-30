package com.google.ar.sceneform.samples.solarsystem


import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Renderable

@DslMarker
annotation class ArDsl

data class Scene(val nodes: List<Node>)


@ArDsl
open class NodeBuilder(var position: Vector3?, var scale: Vector3?, var model: Renderable?) {

    private val nodes = mutableListOf<Node>()

    open fun build(): Node {
        return Node().apply {
            if (position != null) localPosition = position
            if (scale != null) localScale = scale
            if (model != null) renderable = model
            nodes.forEach { it.setParent(this) }
        }
    }

    fun node(position: Vector3? = null,
             scale: Vector3? = null,
             model: Renderable? = null,
             setup: NodeBuilder.() -> Unit = {}) {
        val nodeBuilder = NodeBuilder(position, scale, model)
        nodeBuilder.setup()
        nodes += nodeBuilder.build()
    }
}


@ArDsl
class AnchorNodeBuilder(var anchor: Anchor?) : NodeBuilder(null, null, null) {

    private val nodes = mutableListOf<Node>()

    override fun build(): AnchorNode {
        anchor?.let {
            return AnchorNode(anchor).apply { nodes.forEach { it.setParent(this) } }
        } ?: throw IllegalArgumentException("Anchor cannot be null")
    }
}

@ArDsl
class SceneBuilder {

    private val nodes = mutableListOf<Node>()

    fun anchorNode(anchor: Anchor? = null, setup: AnchorNodeBuilder.() -> Unit = {}) {
        val nodeBuilder = AnchorNodeBuilder(anchor)
        nodeBuilder.setup()
        nodes += nodeBuilder.build()
    }

    fun build(): Scene {
        return Scene(nodes)
    }

}

fun scene(setup: SceneBuilder.() -> Unit): Scene {
    val sceneBuilder = SceneBuilder()
    sceneBuilder.setup()
    return sceneBuilder.build()
}


