package com.github.PluginSkillDead.mapBuilder;

import java.util.concurrent.TimeUnit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

public class RegenBlockPrompt extends ValidatingPrompt
{
  private final Block block;
  private int questionlvl;
  private boolean initial;
  private RegeneratingBlock b;
  private final ChatColor purple = ChatColor.LIGHT_PURPLE;
  private final ChatColor gold = ChatColor.GOLD;
  private final ChatColor red = ChatColor.RED;
  private final ChatColor green = ChatColor.GREEN;
  private Material mat;
  private Integer dataVal = Integer.valueOf(-1);
  private boolean regenerate = true;
  private boolean cobbleReplace = true;
  private boolean naturalBreak = false;
  private int time = 0;
  private TimeUnit unit = null;
  private int xp = 0;
  private Material product = null;
  private String amount = null;
  private Integer productData = Integer.valueOf(-1);
  private String effect = null;

  public RegenBlockPrompt(Block block)
  {
    this.block = block;
    this.mat = block.getType();
    this.questionlvl = 0;
    this.initial = true;

    this.b = RegeneratingBlock.getRegeneratingBlock(block.getType(), Integer.valueOf(-1));
    if (this.b == null)
      this.b = RegeneratingBlock.getRegeneratingBlock(block.getType(), Integer.valueOf(block.getData()));
  }

  public String getPromptText(ConversationContext context)
  {
    String finalMessage = "";
    if (this.initial)
    {
      this.initial = false;
      context.getForWhom().sendRawMessage(this.purple + "Welcome to the " + this.gold + "Regenerating Block Helper!");
    }

    switch (this.questionlvl)
    {
    case 0:
    default:
      context.getForWhom().sendRawMessage(this.purple + "You have selected a block of type " + this.gold + this.block.getType().name() + this.purple + ".");
      context.getForWhom().sendRawMessage(this.purple + "At any time you may go back one question by typing " + this.green + "Back" + this.purple + ".");
      context.getForWhom().sendRawMessage(this.purple + "You may also exit at anytime by typing " + this.red + "Quit" + this.purple + ".");
      finalMessage = this.purple + "If you with to continue, type " + this.green + "Ok" + this.purple + " otherwise type " + this.red + "Quit" + this.purple + ".";
      break;
    case 1:
      if (this.b != null)
      {
        if (this.b.MaterialData == this.block.getData())
        {
          context.getForWhom().sendRawMessage(this.purple + "A regenerating block of this type with this data value already exists.");
          context.getForWhom().sendRawMessage(this.purple + "If you would like to remove it, type " + this.green + "Remove" + this.purple + ".");
          finalMessage = this.purple + "If you would like to override it, type " + this.green + "Override" + this.purple + ", otherwise type " + this.red + "Quit" + this.purple + ".";
        }
        else if (this.b.MaterialData == -1)
        {
          context.getForWhom().sendRawMessage(this.purple + "A regenerating block has already been specified for all data values of this type.");
          finalMessage = this.purple + "If you would like to remove it, type " + this.green + "Remove" + this.purple + " otherwise type " + this.red + "Quit" + this.purple + ".";
        }
      }
      else
      {
        this.questionlvl = 4;
        return getPromptText(context);
      }

      break;
    case 2:
      break;
    case 3:
      break;
    case 4:
      context.getForWhom().sendRawMessage(this.purple + "Would you like these settings to apply to all blocks of this type or just this data value?");
      finalMessage = this.purple + "Type either " + this.green + "This" + this.purple + " or " + this.green + "All" + this.purple + ".";
      break;
    case 5:
      context.getForWhom().sendRawMessage(this.purple + "Would you like this block to be just unbreakable, or would you like it to regenerate?");
      finalMessage = this.purple + "Type either " + this.green + "Unbreakable" + this.purple + " or " + this.green + "Regenerate" + this.purple + ".";
      break;
    case 6:
      context.getForWhom().sendRawMessage(this.purple + "Would you like this block to break naturally or would you like the items to be added to the players's inventory?");
      finalMessage = this.purple + "Type either " + this.green + "Natural" + this.purple + " or " + this.green + "UnNatural" + this.purple + ".";
      break;
    case 7:
      context.getForWhom().sendRawMessage(this.purple + "How long would you like this block to remain broken before it is regenerated?");
      finalMessage = this.purple + "Enter a value in the format: " + this.red + "[" + this.green + "Number" + this.red + "] [" + this.green + "Unit" + this.red + "]" + this.purple + " (omit the brackets)";
      break;
    case 8:
      context.getForWhom().sendRawMessage(this.purple + "When it is broken, would you like this block to be replaced by cobblestone or air?");
      finalMessage = this.purple + "Type either " + this.green + "Cobblestone" + this.purple + " or " + this.green + "Air" + this.purple + ".";
      break;
    case 9:
      context.getForWhom().sendRawMessage(this.purple + "How much XP do you want to give?");
      finalMessage = this.purple + "Enter a " + this.green + "Number" + this.purple + " greater than -1.";
      break;
    case 10:
      context.getForWhom().sendRawMessage(this.purple + "If you would like to use a special effect, enter it.");
      finalMessage = this.purple + "Type either an " + this.green + "Effect" + this.purple + " or " + this.green + "None" + this.purple + ".";
      break;
    case 11:
      context.getForWhom().sendRawMessage(this.purple + "What type of product would you like to give when this block is broken?");
      context.getForWhom().sendRawMessage(this.purple + "Enter a material value and/or a data value in the format:" + this.red + "[" + this.green + "Material" + this.red + "] [" + this.green + "DataValue" + this.red + "]" + this.purple + "(omit brackets)");
      finalMessage = this.purple + "Material enum reference: " + ChatColor.RESET + "http://jd.bukkit.org/rb/apidocs/org/bukkit/Material.html";
      break;
    case 12:
      context.getForWhom().sendRawMessage(this.purple + "How much product would you like to give?");
      finalMessage = this.purple + "Enter a " + this.green + "Number" + this.purple + ".";
    }

    return finalMessage;
  }

  protected Prompt acceptValidatedInput(ConversationContext context, String input)
  {
    if (input.startsWith("/")) {
      input = input.substring(1);
    }
    input = input.toLowerCase().trim();

    if ((input.equals("quit")) || (input.equals("exit")) || (input.equals("stop")))
    {
      context.getForWhom().sendRawMessage(ChatColor.GOLD + "Regenerating Block Helper" + ChatColor.LIGHT_PURPLE + " has been closed.");
      return Prompt.END_OF_CONVERSATION;
    }
    if ((input.equals("back")) || (input.equals("previous")))
    {
      if (this.questionlvl == 4)
        this.questionlvl = 0;
      else if (this.questionlvl == 0)
        this.questionlvl = 0;
      else
        this.questionlvl -= 1;
      return this;
    }

    switch (this.questionlvl)
    {
    case 0:
      if (input.equals("ok"))
      {
        this.questionlvl = 1;
      }
      break;
    case 1:
      if (this.b.MaterialData == this.block.getData())
      {
        if (input.equals("remove"))
        {
          if (RegeneratingBlock.removeRegeneratingBlock(this.b.Type, Integer.valueOf(this.b.MaterialData)))
            context.getForWhom().sendRawMessage(this.purple + "The regenerating block has been removed.");
          else
            context.getForWhom().sendRawMessage(this.purple + "There was an error while removing the regenerating block.");
          return endBlockHelper(context);
        }
        if (input.equals("override"))
        {
          this.questionlvl = 4;
        }
      }
      else if (this.b.MaterialData == -1)
      {
        if (input.equals("remove"))
        {
          if (RegeneratingBlock.removeRegeneratingBlock(this.b.Type, Integer.valueOf(this.b.MaterialData)))
            context.getForWhom().sendRawMessage(this.purple + "The regenerating block has been removed.");
          else
            context.getForWhom().sendRawMessage(this.purple + "There was an error while removing the regenerating block.");
          return endBlockHelper(context);
        }

      }

      break;
    case 2:
      break;
    case 3:
      break;
    case 4:
      if (input.equals("this"))
      {
        this.dataVal = Integer.valueOf(this.block.getData());
        this.questionlvl = 5;
      }
      else if (input.equals("all"))
      {
        this.dataVal = Integer.valueOf(-1);
        this.questionlvl = 5;
      }
      break;
    case 5:
      if (input.equals("unbreakable"))
      {
        this.regenerate = false;
        return saveBlockAndQuit(context);
      }
      if (input.equals("regenerate"))
      {
        this.regenerate = true;
        this.questionlvl = 6;
      }
      break;
    case 6:
      if (input.equals("natural"))
      {
        this.naturalBreak = true;

        this.questionlvl = 7;
      }
      else if (input.equals("unnatural"))
      {
        this.naturalBreak = false;
        this.questionlvl = 7;
      }
      break;
    case 7:
      String[] args = input.split(" ");
      if (args.length == 2)
      {
        try
        {
          int number = Integer.parseInt(args[0]);
          TimeUnit u = MapBuilder.getUnit(args[1]);
          if (u != null)
          {
            this.time = number;
            this.unit = u;

            if (this.naturalBreak) {
              return saveBlockAndQuit(context);
            }
            this.questionlvl = 8;
          }

        }
        catch (Exception localException)
        {
        }

      }

      break;
    case 8:
      if (input.equals("cobblestone"))
      {
        this.cobbleReplace = true;
        this.questionlvl = 9;
      }
      else if (input.equals("air"))
      {
        this.cobbleReplace = false;
        this.questionlvl = 9;
      }
      break;
    case 9:
      try
      {
        int num = Integer.parseInt(input);
        this.xp = num;
        this.questionlvl = 10;
      }
      catch (Exception localException1)
      {
      }

    case 10:
      if (input.equals("none"))
      {
        this.effect = null;
        this.questionlvl = 11;
      }
      else if (input.equals("gravel"))
      {
        this.effect = "Gravel";
        return saveBlockAndQuit(context);
      }

      break;
    case 11:
      String[] args = input.split(" ");
      try
      {
        if (args.length == 1)
          this.productData = Integer.valueOf(-1);
        else
          this.productData = Integer.valueOf(Integer.parseInt(args[1]));
        args[0] = args[0].toUpperCase().replace(" ", "_");
        Material m = Material.getMaterial(args[0]);
        if (m != null)
        {
          this.product = m;
          this.questionlvl = 12;
        }

      }
      catch (Exception localException2)
      {
      }

    case 12:
      try
      {
        if (input.contains("random"))
        {
          String x = input.split(",")[0];
          String y = input.split(",")[1];
          x = x.substring(7);
          y = y.substring(0, y.length() - 1);
          try
          {
            Integer.parseInt(x);
            Integer.parseInt(y);
            this.amount = input.toUpperCase();
            return saveBlockAndQuit(context);
          }
          catch (NumberFormatException localNumberFormatException)
          {
          }

        }
        else
        {
          Integer r = Integer.valueOf(Integer.parseInt(input));
          this.amount = r.toString();
          return saveBlockAndQuit(context);
        }

      }
      catch (Exception localException3)
      {
      }

    }

    return this;
  }

  private Prompt saveBlockAndQuit(ConversationContext context)
  {
    RegeneratingBlock.addRegeneratingBlock(new RegeneratingBlock(this.mat, this.dataVal.intValue(), this.regenerate, this.cobbleReplace, this.naturalBreak, this.time, 
      this.unit, this.xp, this.product, this.amount, this.productData.intValue(), this.effect));
    return endBlockHelper(context);
  }

  private Prompt endBlockHelper(ConversationContext context)
  {
    context.getForWhom().sendRawMessage(this.purple + "These regenerating block settings have been saved.");
    context.getForWhom().sendRawMessage(ChatColor.GOLD + "Regenerating Block Helper" + ChatColor.LIGHT_PURPLE + " has been closed.");
    return Prompt.END_OF_CONVERSATION;
  }

  protected boolean isInputValid(ConversationContext context, String input)
  {
    return true;
  }
}
