package com.github.lppedd.cc.lookupElement

import com.github.lppedd.cc.*
import com.github.lppedd.cc.completion.providers.FooterValueProviderWrapper
import com.github.lppedd.cc.parser.CCParser
import com.github.lppedd.cc.parser.ValidToken
import com.github.lppedd.cc.psiElement.CommitFooterValuePsiElement
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.openapi.util.TextRange

/**
 * @author Edoardo Luppi
 */
internal class CommitFooterLookupElement(
    override val index: Int,
    override val provider: FooterValueProviderWrapper,
    private val psiElement: CommitFooterValuePsiElement,
    private val completionPrefix: String,
) : CommitLookupElement() {
  override val priority = PRIORITY_FOOTER

  override fun getPsiElement(): CommitFooterValuePsiElement =
    psiElement

  override fun getLookupString(): String =
    psiElement.commitFooterValue.value

  override fun renderElement(presentation: LookupElementPresentation) {
    presentation.icon = ICON_FOOTER
    presentation.itemText = lookupString.flattenWhitespaces().abbreviate(100)
    presentation.isTypeIconRightAligned = true
  }

  override fun handleInsert(context: InsertionContext) {
    val editor = context.editor
    val document = context.document

    val (lineStart, lineEnd) = document.getLineRangeByOffset(context.startOffset)
    val tempAdditionalLength = lookupString.length - completionPrefix.length
    val removeTo = context.tailOffset - lineStart
    val removeFrom = removeTo - tempAdditionalLength
    val oldFooterText =
      document
        .getSegment(lineStart, document.textLength)
        .removeRange(removeFrom, removeTo)

    val footer = CCParser.parseFooter(oldFooterText).footer
    val footerText = " $lookupString"
    val (footerStart, footerEnd) = if (footer is ValidToken) {
      val (start, end) = footer.range
      TextRange(lineStart + start, lineStart + end + tempAdditionalLength)
    } else {
      TextRange(lineStart, lineEnd)
    }

    document.replaceString(footerStart, footerEnd, footerText)
    editor.moveCaretToOffset(footerStart + footerText.length)
  }
}
