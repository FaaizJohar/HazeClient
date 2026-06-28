package com.cavrix.hazecore;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * Injects rendering and input hooks into the Minecraft game loop.
 */
public class MinecraftTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        
        // Example logic to hook into Minecraft's GameRenderer
        if ("net/minecraft/client/renderer/GameRenderer".equals(className)) {
            System.out.println("[HazeCore] Transforming GameRenderer...");
            try {
                ClassNode classNode = new ClassNode();
                ClassReader classReader = new ClassReader(classfileBuffer);
                classReader.accept(classNode, 0);

                for (MethodNode method : classNode.methods) {
                    if (method.name.equals("render")) {
                        InsnList list = new InsnList();
                        list.add(new InsnNode(Opcodes.FCONST_0));
                        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/cavrix/hazecore/render/RenderEngine", "onRender", "(F)V", false));
                        method.instructions.insert(list);
                    }
                }

                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                classNode.accept(classWriter);
                return classWriter.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return classfileBuffer;
    }
}
