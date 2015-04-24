package com.github.PluginSkillDead.mapBuilder;

import org.bukkit.Bukkit;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public final class SingleQuestionPrompt extends ValidatingPrompt
{
  private static ConversationFactory factory = new ConversationFactory(Bukkit.getPluginManager().getPlugin("Annihilation"));
  private final Player player;
  private final String question;
  private final AcceptAnswer listener;

  public SingleQuestionPrompt(Player player, String question, AcceptAnswer listener)
  {
    this.player = player;
    this.question = question;
    this.listener = listener;
  }

  public String getPromptText(ConversationContext context)
  {
    return this.question;
  }

  protected Prompt acceptValidatedInput(ConversationContext context, String input)
  {
    if ((input.equalsIgnoreCase("quit")) || (input.equalsIgnoreCase("stop")) || (input.equalsIgnoreCase("end"))) {
      return Prompt.END_OF_CONVERSATION;
    }
    if (this.listener.onAnswer(input)) {
      return Prompt.END_OF_CONVERSATION;
    }
    return this;
  }

  protected boolean isInputValid(ConversationContext context, String input)
  {
    return true;
  }

  public void run()
  {
    if (!this.player.isConversing())
    {
      Conversation conv = factory.withModality(false).withFirstPrompt(this).withLocalEcho(true).buildConversation(this.player);
      conv.begin();
    }
  }
}