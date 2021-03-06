package com.github.lppedd.cc.api

import com.intellij.openapi.extensions.ProjectExtensionPointName
import org.jetbrains.annotations.ApiStatus

internal val BODY_EP = ProjectExtensionPointName<CommitBodyProvider>(
  "com.github.lppedd.idea-conventional-commit.commitBodyProvider"
)

/**
 * @author Edoardo Luppi
 */
@ApiStatus.Experimental
@ApiStatus.AvailableSince("0.8.0")
interface CommitBodyProvider : CommitTokenProvider {
  fun getCommitBodies(
      commitType: String?,
      commitScope: String?,
      commitSubject: String?,
  ): Collection<CommitBody>
}

open class CommitBody @JvmOverloads constructor(
    @get:JvmName("getText")
    val value: String,
    val description: String = "",
) : CommitTokenElement()
