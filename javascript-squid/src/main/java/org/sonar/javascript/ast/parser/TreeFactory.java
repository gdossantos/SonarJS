/*
 * SonarQube JavaScript Plugin
 * Copyright (C) 2011 SonarSource and Eriks Nukis
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.javascript.ast.parser;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.javascript.model.implementations.expression.IdentifierTreeImpl;
import org.sonar.javascript.model.implementations.lexical.InternalSyntaxToken;
import org.sonar.javascript.model.implementations.statement.BlockTreeImpl;
import org.sonar.javascript.model.implementations.statement.BreakStatementTreeImpl;
import org.sonar.javascript.model.implementations.statement.CaseClauseTreeImpl;
import org.sonar.javascript.model.implementations.statement.CatchBlockTreeImpl;
import org.sonar.javascript.model.implementations.statement.ContinueStatementTreeImpl;
import org.sonar.javascript.model.implementations.statement.DebuggerStatementTreeImpl;
import org.sonar.javascript.model.implementations.statement.DefaultClauseTreeImpl;
import org.sonar.javascript.model.implementations.statement.DoWhileStatementTreeImpl;
import org.sonar.javascript.model.implementations.statement.ElseClauseTreeImpl;
import org.sonar.javascript.model.implementations.statement.EmptyStatementTreeImpl;
import org.sonar.javascript.model.implementations.statement.ExpressionStatementTreeImpl;
import org.sonar.javascript.model.implementations.statement.ForInStatementTreeImpl;
import org.sonar.javascript.model.implementations.statement.ForOfStatementTreeImpl;
import org.sonar.javascript.model.implementations.statement.ForStatementTreeImpl;
import org.sonar.javascript.model.implementations.statement.IfStatementTreeImpl;
import org.sonar.javascript.model.implementations.statement.LabelledStatementTreeImpl;
import org.sonar.javascript.model.implementations.statement.ReturnStatementTreeImpl;
import org.sonar.javascript.model.implementations.statement.SwitchStatementTreeImpl;
import org.sonar.javascript.model.implementations.statement.ThrowStatementTreeImpl;
import org.sonar.javascript.model.implementations.statement.TryStatementTreeImpl;
import org.sonar.javascript.model.implementations.statement.VariableDeclarationTreeImpl;
import org.sonar.javascript.model.implementations.statement.VariableStatementTreeImpl;
import org.sonar.javascript.model.implementations.statement.WhileStatementTreeImpl;
import org.sonar.javascript.model.implementations.statement.WithStatementTreeImpl;
import org.sonar.javascript.model.interfaces.statement.CaseClauseTree;
import org.sonar.javascript.model.interfaces.statement.ElseClauseTree;
import org.sonar.javascript.model.interfaces.statement.SwitchClauseTree;
import org.sonar.javascript.model.interfaces.statement.SwitchStatementTree;
import org.sonar.javascript.parser.sslr.Optional;

import java.awt.geom.AffineTransform;
import java.util.List;

public class TreeFactory {

  // Statements

  public EmptyStatementTreeImpl emptyStatement(AstNode semicolon) {
    return new EmptyStatementTreeImpl(InternalSyntaxToken.create(semicolon));
  }

  public DebuggerStatementTreeImpl debuggerStatement(AstNode debuggerWord, AstNode eos) {
    return new DebuggerStatementTreeImpl(InternalSyntaxToken.create(debuggerWord), eos);
  }

  public VariableStatementTreeImpl newVariableStatement(VariableDeclarationTreeImpl variableDeclaration, Optional<List<Tuple<AstNode, VariableDeclarationTreeImpl>>> rest) {
    List<AstNode> children = Lists.newArrayList();
    List<InternalSyntaxToken> commas = Lists.newArrayList();
    List<VariableDeclarationTreeImpl> declarations = Lists.newArrayList();

    declarations.add(variableDeclaration);
    children.add(variableDeclaration);

    if (rest.isPresent()) {
      for (Tuple<AstNode, VariableDeclarationTreeImpl> tuple : rest.get()) {

        commas.add(InternalSyntaxToken.create(tuple.first()));
        declarations.add(tuple.second());

        children.add(tuple.first());
        children.add(tuple.second());
      }
    }
    return new VariableStatementTreeImpl(declarations, commas, children);
  }

  public VariableStatementTreeImpl completeVariableStatement(AstNode varKeyword, VariableStatementTreeImpl partial, AstNode eos) {
    return partial.complete(InternalSyntaxToken.create(varKeyword), eos);
  }

  public VariableDeclarationTreeImpl variableDeclaration(AstNode bindingIdentifierInitialiser) {
    return new VariableDeclarationTreeImpl(bindingIdentifierInitialiser);
  }

  public LabelledStatementTreeImpl labelledStatement(AstNode identifier, AstNode colon, AstNode statement) {
    return new LabelledStatementTreeImpl(new IdentifierTreeImpl(InternalSyntaxToken.create(identifier)), InternalSyntaxToken.create(colon), statement);
  }

  public ContinueStatementTreeImpl completeContinueStatement(AstNode continueToken, ContinueStatementTreeImpl labelOrEndOfStatement) {
    return labelOrEndOfStatement.complete(InternalSyntaxToken.create(continueToken));
  }

  public ContinueStatementTreeImpl newContinueWithLabel(AstNode identifier, AstNode eos) {
    return new ContinueStatementTreeImpl(new IdentifierTreeImpl(InternalSyntaxToken.create(identifier)), eos);
  }

  public ContinueStatementTreeImpl newContinueWithoutLabel(AstNode eos) {
    return new ContinueStatementTreeImpl(eos);
  }

  public BreakStatementTreeImpl completeBreakStatement(AstNode breakToken, BreakStatementTreeImpl labelOrEndOfStatement) {
    return labelOrEndOfStatement.complete(InternalSyntaxToken.create(breakToken));
  }

  public BreakStatementTreeImpl newBreakWithLabel(AstNode identifier, AstNode eos) {
    return new BreakStatementTreeImpl(new IdentifierTreeImpl(InternalSyntaxToken.create(identifier)), eos);
  }

  public BreakStatementTreeImpl newBreakWithoutLabel(AstNode eos) {
    return new BreakStatementTreeImpl(eos);
  }

  public ReturnStatementTreeImpl completeReturnStatement(AstNode returnToken, ReturnStatementTreeImpl expressionOrEndOfStatement) {
    return expressionOrEndOfStatement.complete(InternalSyntaxToken.create(returnToken));
  }

  public ReturnStatementTreeImpl newReturnWithExpression(AstNode expression, AstNode eos) {
    return new ReturnStatementTreeImpl(expression, eos);
  }

  public ReturnStatementTreeImpl newReturnWithoutExpression(AstNode eos) {
    return new ReturnStatementTreeImpl(eos);
  }

  public ThrowStatementTreeImpl newThrowStatement(AstNode throwToken, AstNode expression, AstNode eos) {
    return new ThrowStatementTreeImpl(InternalSyntaxToken.create(throwToken), expression, eos);
  }

  public WithStatementTreeImpl newWithStatement(AstNode withToken, AstNode openingParen, AstNode expression, AstNode closingParen, AstNode statement) {
    return new WithStatementTreeImpl(InternalSyntaxToken.create(withToken), InternalSyntaxToken.create(openingParen), expression, InternalSyntaxToken.create(closingParen), statement);
  }

  public BlockTreeImpl newBlock(AstNode openingCurlyBrace, Optional<AstNode> statements, AstNode closingCurlyBrace) {
    if (statements.isPresent()) {
      return new BlockTreeImpl(InternalSyntaxToken.create(openingCurlyBrace), statements.get(), InternalSyntaxToken.create(closingCurlyBrace));
    }
    return new BlockTreeImpl(InternalSyntaxToken.create(openingCurlyBrace), InternalSyntaxToken.create(closingCurlyBrace));
  }

  public TryStatementTreeImpl newTryStatementWithCatch(CatchBlockTreeImpl catchBlock, Optional<TryStatementTreeImpl> partial) {
    if (partial.isPresent()) {
      return partial.get().complete(catchBlock);
    }
    return new TryStatementTreeImpl(catchBlock);
  }

  public TryStatementTreeImpl newTryStatementWithFinally(AstNode finallyKeyword, BlockTreeImpl block) {
    return new TryStatementTreeImpl(InternalSyntaxToken.create(finallyKeyword), block);
  }

  public TryStatementTreeImpl completeTryStatement(AstNode tryToken, BlockTreeImpl block, TryStatementTreeImpl catchFinallyBlock) {
    return catchFinallyBlock.complete(InternalSyntaxToken.create(tryToken), block);
  }

  public CatchBlockTreeImpl newCatchBlock(AstNode catchToken, AstNode lparenToken, AstNode catchParameter, AstNode rparenToken, BlockTreeImpl block) {
    return new CatchBlockTreeImpl(
      InternalSyntaxToken.create(catchToken),
      InternalSyntaxToken.create(lparenToken),
      catchParameter,
      InternalSyntaxToken.create(rparenToken),
      block);
  }

  public SwitchStatementTreeImpl newSwitchStatement(AstNode openCurlyBrace, Optional<List<CaseClauseTreeImpl>> caseClauseList, Optional<Tuple<DefaultClauseTreeImpl, Optional<List<CaseClauseTreeImpl>>>> defaultAndRestCases, AstNode closeCurlyBrace) {
    List<SwitchClauseTree> cases = Lists.newArrayList();

    // First case list
    if (caseClauseList.isPresent()) {
      cases.addAll(caseClauseList.get());
    }

    // default case
    if (defaultAndRestCases.isPresent()) {
      cases.add(defaultAndRestCases.get().first());

      // case list following default
      if (defaultAndRestCases.get().second().isPresent()) {
        cases.addAll(defaultAndRestCases.get().second().get());
      }
    }

    return new SwitchStatementTreeImpl(InternalSyntaxToken.create(openCurlyBrace), cases, InternalSyntaxToken.create(closeCurlyBrace));
  }

  public SwitchStatementTreeImpl completeSwitchStatement(AstNode switchToken, AstNode openParenthesis, AstNode expression, AstNode closeParenthesis, SwitchStatementTreeImpl caseBlock) {
    return caseBlock.complete(InternalSyntaxToken.create(switchToken), InternalSyntaxToken.create(openParenthesis), expression, InternalSyntaxToken.create(closeParenthesis));
  }

  public DefaultClauseTreeImpl defaultClause(AstNode defaultToken, AstNode colonToken, Optional<AstNode> statementList) {
    if (statementList.isPresent()) {
      return new DefaultClauseTreeImpl(InternalSyntaxToken.create(defaultToken), InternalSyntaxToken.create(colonToken), statementList.get());
    }
    return new DefaultClauseTreeImpl(InternalSyntaxToken.create(defaultToken), InternalSyntaxToken.create(colonToken));
  }

  public CaseClauseTreeImpl caseClause(AstNode caseToken, AstNode expression, AstNode colonToken, Optional<AstNode> statementList) {
    if (statementList.isPresent()) {
      return new CaseClauseTreeImpl(InternalSyntaxToken.create(caseToken), expression, InternalSyntaxToken.create(colonToken), statementList.get());
    }
    return new CaseClauseTreeImpl(InternalSyntaxToken.create(caseToken), expression, InternalSyntaxToken.create(colonToken));
  }

  public ElseClauseTreeImpl elseClause(AstNode elseToken, AstNode statement) {
    return new ElseClauseTreeImpl(InternalSyntaxToken.create(elseToken), statement);
  }

  public IfStatementTreeImpl ifStatement(AstNode ifToken, AstNode openParenToken, AstNode condition, AstNode closeParenToken, AstNode statement, Optional<ElseClauseTreeImpl> elseClause) {
    if (elseClause.isPresent()) {
      return new IfStatementTreeImpl(InternalSyntaxToken.create(ifToken), InternalSyntaxToken.create(openParenToken), condition, InternalSyntaxToken.create(closeParenToken), statement, elseClause.get());
    }
    return new IfStatementTreeImpl(InternalSyntaxToken.create(ifToken), InternalSyntaxToken.create(openParenToken), condition, InternalSyntaxToken.create(closeParenToken), statement);
  }

  public WhileStatementTreeImpl whileStatement(AstNode whileToken, AstNode openParenthesis, AstNode condition, AstNode closeParenthesis, AstNode statetment) {
    return new WhileStatementTreeImpl(InternalSyntaxToken.create(whileToken), InternalSyntaxToken.create(openParenthesis), condition, InternalSyntaxToken.create(closeParenthesis), statetment);
  }

  public DoWhileStatementTreeImpl doWhileStatement(AstNode doToken, AstNode statement, AstNode whileToken, AstNode openParenthesis, AstNode condition, AstNode closeParenthesis, AstNode eos) {
    return new DoWhileStatementTreeImpl(InternalSyntaxToken.create(doToken), statement, InternalSyntaxToken.create(whileToken), InternalSyntaxToken.create(openParenthesis), condition, InternalSyntaxToken.create(closeParenthesis), eos);
  }

  public ExpressionStatementTreeImpl expressionStatement(AstNode expression, AstNode eos) {
    return new ExpressionStatementTreeImpl(expression, eos);
  }

  public ForOfStatementTreeImpl forOfStatement(AstNode forToken, AstNode openParenthesis, AstNode variableOrExpression, AstNode ofToken, AstNode expression, AstNode closeParenthesis, AstNode statement) {
    return new ForOfStatementTreeImpl(
      InternalSyntaxToken.create(forToken),
      InternalSyntaxToken.create(openParenthesis),
      variableOrExpression,
      InternalSyntaxToken.create(ofToken),
      expression, InternalSyntaxToken.create(closeParenthesis),
      statement);
  }

  public ForInStatementTreeImpl forInStatement(AstNode forToken, AstNode openParenthesis, AstNode variableOrExpression, AstNode inToken, AstNode expression, AstNode closeParenthesis, AstNode statement) {
    return new ForInStatementTreeImpl(
      InternalSyntaxToken.create(forToken),
      InternalSyntaxToken.create(openParenthesis),
      variableOrExpression,
      InternalSyntaxToken.create(inToken),
      expression, InternalSyntaxToken.create(closeParenthesis),
      statement);
  }

  // End of statements

  // Helpers

  public static final AstNodeType WRAPPER_AST_NODE = new AstNodeType() {
    @Override
    public String toString() {
      return "WRAPPER_AST_NODE";
    }
  };

  public ForStatementTreeImpl forStatement(AstNode forToken, AstNode openParenthesis, Optional<AstNode> init, AstNode firstSemiToken, Optional<AstNode> condition, AstNode secondSemiToken, Optional<AstNode> update, AstNode closeParenthesis, AstNode statement) {
    List<AstNode> children = Lists.newArrayList();

    children.add(forToken);
    children.add(openParenthesis);
    if (init.isPresent()) {
      children.add(init.get());
    }
    children.add(firstSemiToken);
    if (condition.isPresent()) {
      children.add(condition.get());
    }
    children.add(secondSemiToken);
    if (update.isPresent()) {
      children.add(update.get());
    }
    children.add(closeParenthesis);
    children.add(statement);

    return new ForStatementTreeImpl(
      InternalSyntaxToken.create(forToken),
      InternalSyntaxToken.create(openParenthesis),
      InternalSyntaxToken.create(firstSemiToken),
      InternalSyntaxToken.create(secondSemiToken),
      InternalSyntaxToken.create(closeParenthesis),
      children);
  }


  public static class Tuple<T, U> extends AstNode {

    private final T first;
    private final U second;

    public Tuple(T first, U second) {
      super(WRAPPER_AST_NODE, WRAPPER_AST_NODE.toString(), null);

      this.first = first;
      this.second = second;

      add(first);
      add(second);
    }

    public T first() {
      return first;
    }

    public U second() {
      return second;
    }

    private void add(Object o) {
      if (o instanceof AstNode) {
        addChild((AstNode) o);
      } else if (o instanceof Optional) {
        Optional opt = (Optional) o;
        if (opt.isPresent()) {
          Object o2 = opt.get();
          if (o2 instanceof AstNode) {
            addChild((AstNode) o2);
          } else if (o2 instanceof List) {
            for (Object o3 : (List) o2) {
              Preconditions.checkArgument(o3 instanceof AstNode, "Unsupported type: " + o3.getClass().getSimpleName());
              addChild((AstNode) o3);
            }
          } else {
            throw new IllegalArgumentException("Unsupported type: " + o2.getClass().getSimpleName());
          }
        }
      } else {
        throw new IllegalStateException("Unsupported argument type: " + o.getClass().getSimpleName());
      }
    }

  }

  private <T, U> Tuple<T, U> newTuple(T first, U second) {
    return new Tuple<T, U>(first, second);
  }

  public <T, U> Tuple<T, U> newTuple1(T first, U second) {
    return newTuple(first, second);
  }

  public <T, U> Tuple<T, U> newTuple2(T first, U second) {
    return newTuple(first, second);
  }

  public <T, U> Tuple<T, U> newTuple3(T first, U second) {
    return newTuple(first, second);
  }

  public <T, U> Tuple<T, U> newTuple4(T first, U second) {
    return newTuple(first, second);
  }

  public <T, U> Tuple<T, U> newTuple5(T first, U second) {
    return newTuple(first, second);
  }

  public <T, U> Tuple<T, U> newTuple6(T first, U second) {
    return newTuple(first, second);
  }

  public <T, U> Tuple<T, U> newTuple7(T first, U second) {
    return newTuple(first, second);
  }

  public <T, U> Tuple<T, U> newTuple8(T first, U second) {
    return newTuple(first, second);
  }

  public <T, U> Tuple<T, U> newTuple9(T first, U second) {
    return newTuple(first, second);
  }

  public <T, U> Tuple<T, U> newTuple10(T first, U second) {
    return newTuple(first, second);
  }

  public <T, U> Tuple<T, U> newTuple11(T first, U second) {
    return newTuple(first, second);
  }

  public <T, U> Tuple<T, U> newTuple12(T first, U second) {
    return newTuple(first, second);
  }

  public <T, U> Tuple<T, U> newTuple13(T first, U second) {
    return newTuple(first, second);
  }

  public <T, U> Tuple<T, U> newTuple14(T first, U second) {
    return newTuple(first, second);
  }

  public <T, U> Tuple<T, U> newTuple15(T first, U second) {
    return newTuple(first, second);
  }

  public <T, U> Tuple<T, U> newTuple16(T first, U second) {
    return newTuple(first, second);
  }

  public <T, U> Tuple<T, U> newTuple17(T first, U second) {
    return newTuple(first, second);
  }

  // End

}