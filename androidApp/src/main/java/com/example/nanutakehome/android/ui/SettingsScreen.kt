package com.example.nanutakehome.android.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nanutakehome.model.SettingsNode
import com.example.nanutakehome.model.sampleSettingsTree
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.rotate
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import com.example.nanutakehome.android.repository.FirebaseUserRepository
import com.example.nanutakehome.model.AppUser
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun SettingsScreen(userId: String) {
    var tree by remember { mutableStateOf(sampleSettingsTree) }
    var isLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(userId) {
        Firebase.firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val storedTree = document.toObject(AppUser::class.java)?.settingsTree
                if (storedTree != null && storedTree.isNotEmpty()) {
                    tree = storedTree
                }
                isLoaded = true
            }
            .addOnFailureListener { e ->
                Log.e("SettingsScreen", "Failed to load settings", e)
                isLoaded = true
            }
    }

    if (!isLoaded) {
        // Optional loading state
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    Button(
        onClick = {
            FirebaseUserRepository.updateSettingsTree(userId, tree)
        },
        modifier = Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth()
    ) {
        Text("Save Settings")
    }
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(tree.size) { index ->
            SettingsNodeView(
                node = tree[index],
                onNodeChanged = { updated ->
                    tree = tree.toMutableList().apply { set(index, updated) }
                }
            )
        }
    }
}


@Composable
fun SettingsNodeView(
    node: SettingsNode,
    onNodeChanged: (SettingsNode) -> Unit,
    indent: Int = 0
) {
    val isLeaf = node.children.isEmpty()

    Column(modifier = Modifier.padding(start = (indent * 16).dp)) {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = node.checked,
                onCheckedChange = { isChecked ->
                    if (isLeaf) {
                        // Just update this node
                        onNodeChanged(node.copy(checked = isChecked))
                    } else {
                        // Update this node + all descendants
                        val updated = updateNodeWithChecked(node, isChecked)
                        onNodeChanged(updated)
                    }
                }
            )
            Text(
                text = node.label,
                style = if (isLeaf) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            if (!isLeaf) {
                IconButton(
                    onClick = {
                        val updatedNode = node.copy(isExpanded = !node.isExpanded)
                        onNodeChanged(updatedNode)
                    }
                ) {
                    val rotationAngle by animateFloatAsState(
                        targetValue = if (node.isExpanded) 180f else 0f,
                        label = "ExpandCollapseRotation"
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (node.isExpanded) "Collapse" else "Expand",
                        modifier = Modifier.rotate(rotationAngle)
                    )
                }
            }
        }

//        if (node.isExpanded && node.children.isNotEmpty()) {
//            node.children.forEachIndexed { i, child ->
//                SettingsNodeView(
//                    node = child,
//                    onNodeChanged = { updatedChild ->
//                        val newChildren = node.children.toMutableList().apply { set(i, updatedChild) }
//                        val allChecked = newChildren.all { it.checked }
//                        val updatedParent = node.copy(
//                            children = newChildren,
//                            checked = allChecked
//                        )
//                        onNodeChanged(updatedParent)
//                    },
//                    indent = indent + 1
//                )
//            }
//        }
        AnimatedVisibility(
            visible = node.isExpanded && node.children.isNotEmpty(),
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                node.children.forEachIndexed { i, child ->
                    SettingsNodeView(
                        node = child,
                        onNodeChanged = { updatedChild ->
                            val updatedChildren = node.children.toMutableList().apply { set(i, updatedChild) }
                            val allChecked = updatedChildren.all { it.checked }
                            val updatedParent = node.copy(
                                children = updatedChildren,
                                checked = allChecked
                            )
                            onNodeChanged(updatedParent)
                        },
                        indent = indent + 1
                    )
                }
            }
        }
    }
}


fun updateNodeWithChecked(node: SettingsNode, isChecked: Boolean): SettingsNode {
    return node.copy(
        checked = isChecked,
        children = node.children.map { updateNodeWithChecked(it, isChecked) }
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsScreen(userId = "hello")
    }
}
