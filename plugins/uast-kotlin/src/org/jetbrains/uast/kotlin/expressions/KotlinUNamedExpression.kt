/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.uast.kotlin

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.ValueArgument
import org.jetbrains.uast.*

class KotlinUNamedExpression private constructor(
        override val name: String?,
        override val uastParent: UElement?,
        expressionProducer: (UElement) -> UExpression
) : UNamedExpression {
    override val expression: UExpression by lz { expressionProducer(this) }

    override val annotations: List<UAnnotation> = emptyList()

    override val psi: PsiElement? = null

    companion object {
        internal fun create(valueArgument: ValueArgument, uastParent: UElement?): UNamedExpression {
            val name = valueArgument.getArgumentName()?.asName?.asString()
            val expression = valueArgument.getArgumentExpression()
            return KotlinUNamedExpression(name, uastParent) { expressionParent ->
                expression?.let { expressionParent.getLanguagePlugin().convert<UExpression>(it, expressionParent) } ?: UastEmptyExpression
            }
        }

        internal fun create(expressions: List<KtExpression>, uastParent: UElement?): UNamedExpression {
            return KotlinUNamedExpression(null, uastParent) { expressionParent ->
                KotlinAnnotationArrayInitializerUCallExpression(expressions, expressionParent)
            }
        }
    }
}