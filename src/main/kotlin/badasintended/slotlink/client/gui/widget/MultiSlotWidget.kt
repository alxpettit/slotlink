package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.init.Networks
import badasintended.slotlink.screen.RequestScreenHandler
import badasintended.slotlink.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.text.*
import net.minecraft.util.Formatting
import kotlin.math.*

/**
 * [MultiSlotWidget] is an impostor.
 */
@Environment(EnvType.CLIENT)
class MultiSlotWidget(
    private val handler: RequestScreenHandler,
    private val index: Int,
    x: Int, y: Int
) : SlotWidget(x, y, 18, handler.playerInventory, { handler.viewedStacks[index].first }) {

    private val count get() = handler.viewedStacks[index].second

    override fun renderOverlay(matrices: MatrixStack, stack: ItemStack) {
        getClient().apply {
            itemRenderer.renderGuiItemOverlay(textRenderer, stack, x + 1, y + 1, "")

            val factor = window.scaleFactor.toFloat()
            val scale = (1 / factor) * ceil(factor / 2)

            val countText = when {
                count <= 1 -> ""
                count < 1000 -> "$count"
                else -> {
                    val exp = (ln(count.toDouble()) / ln(1000.0)).toInt()
                    String.format("%.1f%c", count / 1000.0.pow(exp.toDouble()), "KMGTPE"[exp - 1])
                }
            }

            matrices.push()
            matrices.translate(0.0, 0.0, itemRenderer.zOffset + 200.0)
            matrices.scale(scale, scale, 1f)
            textRenderer.drawWithShadow(
                matrices, countText, ((x + 17 - (textRenderer.getWidth(countText) * scale)) / scale),
                ((y + 17 - (textRenderer.fontHeight * scale)) / scale), 0xFFFFFF
            )
            matrices.pop()
        }
    }

    override fun appendTooltip(tooltip: MutableList<Text>) {
        (tooltip[0] as MutableText).append(LiteralText(" (${count})").formatted(Formatting.GOLD))
    }

    override fun onClick(button: Int) {
        val buf = buf().apply {
            writeVarInt(handler.syncId)
            writeVarInt(index)
            writeVarInt(button)
            writeBoolean(Screen.hasShiftDown())
        }
        c2s(Networks.MULTI_SLOT_CLICK, buf)
    }

}
